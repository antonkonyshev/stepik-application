package com.github.antonkonyshev.stepic.data.network

import com.github.antonkonyshev.stepic.domain.model.Author
import com.github.antonkonyshev.stepic.domain.model.Course
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StepicApi {
    @GET("courses")
    suspend fun fetchCourses(
        @Query("page") page: Long,
        @Query("page_size") pageSize: Int,
        @Query("search") search: String?,
        @Query("order") order: String?
    ): CourseResponse

    @GET("users/{authorId}")
    suspend fun fetchUser(
        @Path("authorId") authorId: Long
    ): UsersResponse

    @POST("users/login")
    @Headers("Referer: https://stepik.org/")
    suspend fun login(@Body credentials: Credentials): Response<Any>

    @GET("stepics/{id}")
    suspend fun loadAccount(@Path("id") id: Long): UsersResponse

    @POST("wish-lists")
    suspend fun addBookmark(@Body wishListPayload: WishListPayload): Response<Any>

    @DELETE("wish-lists/{courseId}")
    suspend fun deleteBookmark(@Path("courseId") courseId: Long): Response<Any>

    companion object {
        const val BASE_URL = "https://stepik.org/api/"
    }
}

data class CourseResponse(val courses: List<Course>, val meta: Meta)
data class Meta(val page: Long, val has_next: Boolean, val has_previous: Boolean)
data class UsersResponse(val users: List<Author>)
data class Credentials(val email: String, val password: String)
data class WishListPayload(val wishList: WishList)
data class WishList(
    val course: String,
    val create_date: String? = null,
    val platform: String = "web",
    val user: String? = null
)