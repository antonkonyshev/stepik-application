package com.github.antonkonyshev.stepic.domain.repository

import com.github.antonkonyshev.stepic.domain.model.Course
import kotlinx.coroutines.flow.StateFlow

interface CourseRepository {
    var hasNext: Boolean
    var hasPrevious: Boolean
    var pageSize: Int
    val searchQuery: StateFlow<String>
    val ordering: StateFlow<Boolean>

    suspend fun getNext(): List<Course>
    suspend fun getPrevious(): List<Course>
    fun clearPagination()
    fun clearFilters()
    fun setSearchQuery(query: String)
    fun setOrdering(order: Boolean)
    fun setFavorite(favorite: Boolean)
}