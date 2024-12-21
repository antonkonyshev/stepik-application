package com.github.antonkonyshev.stepic.data.repository

import android.util.Log
import com.github.antonkonyshev.stepic.data.database.CourseDao
import com.github.antonkonyshev.stepic.data.database.CourseMapper
import com.github.antonkonyshev.stepic.data.network.StepicApi
import com.github.antonkonyshev.stepic.data.network.WishList
import com.github.antonkonyshev.stepic.data.network.WishListPayload
import com.github.antonkonyshev.stepic.domain.model.Course
import com.github.antonkonyshev.stepic.domain.repository.CourseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CourseRepositoryImpl() : CourseRepository, KoinComponent {
    private val api: StepicApi by inject()
    private val courseDao: CourseDao by inject()

    var page: Long = 0
    override var pageSize: Int = 20
    override var hasNext: Boolean = false
    override var hasPrevious: Boolean = false
    private val _searchQuery = MutableStateFlow("")
    override val searchQuery = _searchQuery.asStateFlow()
    private val _ordering = MutableStateFlow(false)
    override val ordering = _ordering.asStateFlow()
    private var favorite: Boolean = false

    private suspend fun getCourses(requestedPage: Long): List<Course> {
        try {
            return api.fetchCourses(
                page = requestedPage, pageSize = pageSize,
                search = when (searchQuery.value.isNotBlank()) {
                    true -> searchQuery.value
                    else -> null
                },
                order = when (ordering.value) {
                    true -> "create_date"
                    else -> null
                },
            ).also {
                hasNext = it.meta.has_next
                hasPrevious = it.meta.has_previous && requestedPage > 1
                if (it.courses.isNotEmpty()) {
                    coroutineScope {
                        launch(Dispatchers.IO) {
                            try {
                                courseDao.upsertCourses(CourseMapper.mapDomainToData(it.courses))
                            } catch (err: Exception) {
                                Log.e(
                                    TAG,
                                    "Error on courses cache update: ${err.toString()} ${err.stackTraceToString()}"
                                )
                            }
                        }
                    }
                }
            }.courses
        } catch (err: Exception) {
            return CourseMapper.mapDataToDomain(
                when (ordering.value) {
                    true -> courseDao.getCoursesReversed(page = requestedPage, pageSize = pageSize)
                    else -> courseDao.getCourses(page = page, pageSize = pageSize)
                }.also {
                    hasNext = it.size >= pageSize
                    hasPrevious = requestedPage > 1
                }
            )
        }
    }

    private suspend fun getFavoriteCourses(requestedPage: Long): List<Course> {
        return CourseMapper.mapDataToDomain(
            when (ordering.value) {
                true -> courseDao.getFavoriteCoursesReversed(
                    page = requestedPage,
                    pageSize = pageSize
                )

                else -> courseDao.getFavoriteCourses(page = requestedPage, pageSize = pageSize)
            }.also {
                hasNext = it.size >= pageSize
                hasPrevious = requestedPage > 1
            }
        )
    }

    override suspend fun getNext(): List<Course> {
        try {
            if (favorite) {
                return getFavoriteCourses(++page)
            }

            var courses = getCourses(++page)
            // Sometimes stepik API endpoint returns empty lists with "has_next" equal to true
            // and not empty lists on the further pages. So we're using a recursion here until
            // we get "has_next" equal to false or some content.
            if (courses.isEmpty() && hasNext) {
                courses = getNext()
            } else {
                supervisorScope { launch { loadFavoriteAttributes(courses) } }
            }

            return courses
        } catch (err: Exception) {
            Log.e(TAG, "Error on courses list loading: ${err.toString()}")
            return emptyList()
        }
    }

    override suspend fun getPrevious(): List<Course> {
        try {
            if (favorite) {
                return getFavoriteCourses(--page)
            }

            var courses = getCourses(--page)
            // Sometimes stepik API endpoint returns empty lists with "has_previous" equal to true
            // and not empty lists on the previous pages. So we're using a recursion here until
            // we get "has_previous" equal to false or some content.
            if (courses.isEmpty() && hasPrevious) {
                courses = getPrevious()
            } else {
                supervisorScope { launch { loadFavoriteAttributes(courses) } }
            }

            return courses
        } catch (err: Exception) {
            Log.e(TAG, "Error on courses list loading: ${err.toString()}")
            return emptyList()
        }
    }

    private suspend fun loadFavoriteAttributes(courses: List<Course>) {
        courses.forEach { it.is_favorite = courseDao.getFavoriteAttribute(it.id) }
    }

    override fun clearPagination() {
        page = 0
        hasNext = false
        hasPrevious = false
    }

    override fun clearFilters() {
        _searchQuery.value = ""
        _ordering.value = false
    }

    override fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    override fun setOrdering(order: Boolean) {
        _ordering.value = order
    }

    override fun setFavorite(newFavorite: Boolean) {
        favorite = newFavorite
    }

    override suspend fun updateBookmark(course: Course) {
        try {
            courseDao.setFavorite(course.id, course.is_favorite)
            if (course.is_favorite) {
                api.addBookmark(WishListPayload(wishList = WishList(course = course.id.toString())))
            } else {
                api.deleteBookmark(course.id)
            }
        } catch (err: Exception) {
            Log.e(TAG, "Error on a course bookmark setting: ${err.toString()}")
        }
    }

    companion object {
        private val TAG = "CourseRepositoryImpl"
    }
}