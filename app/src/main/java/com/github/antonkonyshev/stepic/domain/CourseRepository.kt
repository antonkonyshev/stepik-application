package com.github.antonkonyshev.stepic.domain

import kotlinx.coroutines.flow.StateFlow

interface CourseRepository {
    var hasNext: Boolean
    var hasPrevious: Boolean
    var pageSize: Int
    val searchQuery: StateFlow<String>

    suspend fun getNext(): List<Course>
    suspend fun getPrevious(): List<Course>
    fun clearPagination()
    fun clearFilters()
    fun setSearchQuery(query: String)
}