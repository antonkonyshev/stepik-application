package com.github.antonkonyshev.stepic

import com.github.antonkonyshev.stepic.data.database.CourseData
import com.github.antonkonyshev.stepic.data.database.CourseMapper
import com.github.antonkonyshev.stepic.domain.model.Course
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals

class CourseMapperTestCase {
    val course = Course(
        123, "Testing title", "Testing summary", "Testing description",
        "https://localhost/cover.jpg", "", "", 9.5f,
        false, "", Date(), listOf(12), false
    )

    @Test
    fun testToData() {
        assertEquals(
            CourseData(
                course.id, course.title, course.summary, course.description,
                course.cover, course.canonical_url, course.continue_url, course.readiness,
                course.is_paid, course.display_price, course.create_date, course.authors[0],
                course.is_favorite
            ),
            CourseMapper.mapDomainToData(course)
        )
    }

    @Test
    fun testToDomain() {
        assertEquals(
            course,
            CourseMapper.mapDataToDomain(
                CourseData(
                    course.id, course.title, course.summary, course.description,
                    course.cover, course.canonical_url, course.continue_url, course.readiness,
                    course.is_paid, course.display_price, course.create_date, course.authors[0],
                    course.is_favorite
                )
            )
        )
    }

    @Test
    fun testCollectionToData() {
        assertEquals(
            listOf(
                CourseData(
                    course.id, course.title, course.summary, course.description,
                    course.cover, course.canonical_url, course.continue_url, course.readiness,
                    course.is_paid, course.display_price, course.create_date, course.authors[0],
                    course.is_favorite
                )
            ),
            listOf(CourseMapper.mapDomainToData(course))
        )
    }

    @Test
    fun testCollectionToDomain() {
        assertEquals(
            listOf(course),
            listOf(
                CourseMapper.mapDataToDomain(
                    CourseData(
                        course.id, course.title, course.summary, course.description,
                        course.cover, course.canonical_url, course.continue_url, course.readiness,
                        course.is_paid, course.display_price, course.create_date, course.authors[0],
                        course.is_favorite
                    )
                )
            )
        )
    }
}