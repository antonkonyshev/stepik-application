package com.github.antonkonyshev.stepic.data

import com.github.antonkonyshev.stepic.domain.Course
import retrofit2.http.GET
import retrofit2.http.Query

interface StepicApi {
    @GET("courses")
    suspend fun fetchCourses(
        @Query("page") page: Long,
        @Query("page_size") pageSize: Int,
        @Query("search") search: String?
    ): CourseResponse

    companion object {
        const val BASE_URL = "https://stepik.org/api/"
    }
}

data class CourseResponse(val courses: List<Course>, val meta: Meta)
data class Meta(val page: Long, val has_next: Boolean, val has_previous: Boolean)