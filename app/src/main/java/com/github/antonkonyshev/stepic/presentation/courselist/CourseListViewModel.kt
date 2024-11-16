package com.github.antonkonyshev.stepic.presentation.courselist

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.antonkonyshev.stepic.domain.model.Course
import com.github.antonkonyshev.stepic.domain.repository.CourseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.reflect.KSuspendFunction1

class CourseListViewModel() : ViewModel(), KoinComponent {
    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses = _courses.asStateFlow()
    private val _loading = MutableStateFlow<Boolean>(true)
    val loading = _loading.asStateFlow()

    val courseRepository: CourseRepository by inject()

    init {
        courseRepository.clearPagination()
        viewModelScope.launch(Dispatchers.IO) {
            loadNext()
        }
    }

    suspend fun loadNext(onLoaded: () -> Unit = {}) {
        _loading.value = true
        val newCourses = courseRepository.getNext()
        newCourses.filter { it.id !in courses.value.map { it.id } }
        appendCourses(newCourses)
        if (courses.value.size < 10) {
            loadNext(onLoaded)
        } else {
            _loading.value = false
            onLoaded()
        }
    }

    suspend fun loadPrevious(onLoaded: () -> Unit = {}) {
        _loading.value = true
        var newCourses = courseRepository.getPrevious()
        newCourses.filter { it.id !in courses.value.map { it.id } }
        if (newCourses.size == 0) {
            if (courseRepository.hasPrevious) {
                loadPrevious(onLoaded)
                return
            }
        } else {
            prependCourses(newCourses)
        }
        _loading.value = false
        onLoaded()
    }

    private fun appendCourses(newCourses: List<Course>) {
        if (_courses.value.size > courseRepository.pageSize * 2) {
            _courses.value = _courses.value.drop(courseRepository.pageSize).plus(newCourses)
        } else {
            _courses.value = _courses.value.plus(newCourses)
        }
    }

    private fun prependCourses(newCourses: List<Course>) {
        if (_courses.value.size > courseRepository.pageSize * 2) {
            _courses.value = newCourses + _courses.value.dropLast(courseRepository.pageSize)
        } else {
            _courses.value = newCourses + _courses.value
        }
    }

    fun loadFurther(
        loadingMethod: KSuspendFunction1<() -> Unit, Unit>,
        listState: LazyListState,
        uiScope: CoroutineScope?
    ) {
        if (!loading.value && courseRepository.hasPrevious) {
            viewModelScope.launch(Dispatchers.IO) {
                val scrollPositionKey = courses.value.getOrNull(listState.firstVisibleItemIndex)?.id
                val scrollOffset = listState.firstVisibleItemScrollOffset
                loadingMethod {
                    uiScope?.launch {
                        if (scrollPositionKey != null) {
                            courses.value.indexOfFirst { it.id == scrollPositionKey }.let { idx ->
                                if (idx >= 0) {
                                    listState.scrollToItem(idx, scrollOffset)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun applySearchFilter(query: String) {
        courseRepository.setSearchQuery(query)
        courseRepository.clearPagination()
        _courses.value = emptyList()
        viewModelScope.launch(Dispatchers.IO) {
            loadNext()
        }
    }

    fun toggleOrdering() {
        courseRepository.setOrdering(!courseRepository.ordering.value)
        courseRepository.clearPagination()
        _courses.value = emptyList()
        viewModelScope.launch(Dispatchers.IO) {
            loadNext()
        }
    }

    fun changeScreen(favorite: Boolean = false) {
        courseRepository.clearFilters()
        courseRepository.clearPagination()
        courseRepository.setFavorite(favorite)
        _courses.value = emptyList()
        viewModelScope.launch(Dispatchers.IO) {
            loadNext()
        }
    }

    fun toggleFavorite(course: Course) {
        TODO("implement")
    }
}