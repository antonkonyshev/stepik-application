package com.github.antonkonyshev.stepic.data

import android.util.Log
import com.github.antonkonyshev.stepic.domain.Course
import com.github.antonkonyshev.stepic.domain.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CourseRepositoryImpl(private val api: StepicApi) : CourseRepository {
    private var page: Long = 0
    override var pageSize: Int = 20
    override var hasNext: Boolean = false
    override var hasPrevious: Boolean = false
    private val _searchQuery = MutableStateFlow("")
    override val searchQuery = _searchQuery.asStateFlow()
    private val _ordering = MutableStateFlow(false)
    override val ordering = _ordering.asStateFlow()
    private var favorite: Boolean = false

    override suspend fun getNext(): List<Course> {
        try {
            val response = api.fetchCourses(
                page = ++page, pageSize = pageSize,
                search = when (searchQuery.value.isNotBlank()) {
                    true -> searchQuery.value
                    else -> null
                },
                order = when (ordering.value) {
                    true -> "create_date"
                    else -> null
                },
            )
            hasNext = response.meta.has_next
            var courses = response.courses
            // Sometimes stepik API endpoint returns empty lists with "has_next" equal to true
            // and not empty lists on the further pages. So we're using a recursion here until
            // we get "has_next" equal to false or some content.
            if (courses.isEmpty() && hasNext) {
                courses = getNext()
            }
            if (favorite) {  // TODO: keep favorite courses IDs in DB
                courses = courses.filter { it.is_favorite }
            }
            return courses
        } catch (err: Exception) {
            Log.e(TAG, "Error on courses list loading: ${err.toString()}")
            TODO("Implement loading from cache")
            return emptyList()
        }
    }

    override suspend fun getPrevious(): List<Course> {
        try {
            val response = api.fetchCourses(
                page = --page, pageSize = pageSize,
                search = when (searchQuery.value.isNotBlank()) {
                    true -> searchQuery.value
                    else -> null
                }, order = when (ordering.value) {
                    true -> "craete_date"
                    else -> null
                }
            )
            hasPrevious = response.meta.has_previous && page > 1
            var courses = response.courses
            // Sometimes stepik API endpoint returns empty lists with "has_previous" equal to true
            // and not empty lists on the previous pages. So we're using a recursion here until
            // we get "has_previous" equal to false or some content.
            if (courses.isEmpty() && hasPrevious) {
                courses = getPrevious()
            }
            if (favorite) {  // TODO: Keep favorite courses IDs in DB
                courses = courses.filter { it.is_favorite }
            }
            return courses
        } catch (err: Exception) {
            Log.e(TAG, "Error on courses list loading: ${err.toString()}")
            TODO("Implement loading from cache")
            return emptyList()
        }
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

    companion object {
        private val TAG = "CourseRepositoryImpl"
    }
}