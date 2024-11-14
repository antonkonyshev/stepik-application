package com.github.antonkonyshev.stepic.domain

interface CourseRepository {
    var hasNext: Boolean
    var hasPrevious: Boolean
    var pageSize: Int

    suspend fun getNext(): List<Course>
    suspend fun getPrevious(): List<Course>
    fun clearPagination()
}