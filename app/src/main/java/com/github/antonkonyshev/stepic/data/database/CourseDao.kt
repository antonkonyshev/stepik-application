package com.github.antonkonyshev.stepic.data.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import java.util.Date

@Dao
interface CourseDao {
    @Upsert
    suspend fun upsertCourses(courses: List<CourseData>)

    @Upsert
    suspend fun upsertCourses(vararg courses: CourseData)

    @Upsert
    suspend fun upsertCourse(course: CourseData)

    @Query("select * from course where id = :id")
    suspend fun getCourseById(id: Long): CourseData

    @Query(
        "select * from course where is_favorite = true order by create_date desc " +
                "limit :pageSize offset (:page - 1) * :pageSize"
    )
    suspend fun getFavoriteCourses(page: Long, pageSize: Int): List<CourseData>

    @Query(
        "select * from course where is_favorite = true order by create_date asc " +
                "limit :pageSize offset (:page - 1) * :pageSize"
    )
    suspend fun getFavoriteCoursesReversed(page: Long, pageSize: Int): List<CourseData>

    @Query(
        "select * from course order by create_date desc " +
                "limit :pageSize offset :page * :pageSize"
    )
    suspend fun getCourses(page: Long, pageSize: Int): List<CourseData>

    @Query(
        "select * from course order by create_date asc " +
                "limit :pageSize offset :page * :pageSize"
    )
    suspend fun getCoursesReversed(page: Long, pageSize: Int): List<CourseData>

    @Query("update course set is_favorite = :favorite where id = :id")
    suspend fun setFavorite(id: Long, favorite: Boolean)

    @Query("select is_favorite from course where id = :id limit 1")
    suspend fun getFavoriteAttribute(id: Long): Boolean
}

@Entity(tableName = "course")
data class CourseData(
    @PrimaryKey val id: Long,
    val title: String,
    val summary: String,
    val description: String,
    val cover: String,
    val canonical_url: String,
    val continue_url: String,
    val readiness: Float,
    val is_paid: Boolean,
    val display_price: String,
    val create_date: Date?,
    val author: Long?,
    var is_favorite: Boolean = false,
)