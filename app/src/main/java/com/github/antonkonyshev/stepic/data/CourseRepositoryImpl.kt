package com.github.antonkonyshev.stepic.data

import android.util.Log
import com.github.antonkonyshev.stepic.domain.Course
import com.github.antonkonyshev.stepic.domain.CourseRepository

class CourseRepositoryImpl(private val api: StepicApi) : CourseRepository {
    private var page: Long = 0
    override var pageSize: Int = 20
    override var hasNext: Boolean = false
    override var hasPrevious: Boolean = false

    override suspend fun getNext(): List<Course> {
        try {
            val response = api.fetchCourses(++page, pageSize)
            hasNext = response.meta.has_next
            var courses = response.courses
            // Sometimes stepik API endpoint returns empty lists with "has_next" equal to true
            // and not empty lists on the further pages. So we're using a recursion here until
            // we get "has_next" equal to false or some content.
            if (courses.isEmpty() && hasNext) {
                courses = getNext()
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
            val response = api.fetchCourses(--page, pageSize)
            hasPrevious = response.meta.has_previous && page > 1
            var courses = response.courses
            // Sometimes stepik API endpoint returns empty lists with "has_previous" equal to true
            // and not empty lists on the previous pages. So we're using a recursion here until
            // we get "has_previous" equal to false or some content.
            if (courses.isEmpty() && hasPrevious) {
                courses = getPrevious()
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

    companion object {
        private val TAG = "CourseRepositoryImpl"
    }
}