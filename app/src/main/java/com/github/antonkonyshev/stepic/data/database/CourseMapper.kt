package com.github.antonkonyshev.stepic.data.database

import com.github.antonkonyshev.stepic.domain.model.Course

object CourseMapper {
    fun mapDataToDomain(course: CourseData): Course {
        return Course(
            id = course.id,
            title = course.title,
            summary = course.summary,
            description = course.description,
            cover = course.cover,
            canonical_url = course.canonical_url,
            continue_url = course.continue_url,
            readiness = course.readiness,
            is_paid = course.is_paid,
            display_price = course.display_price,
            create_date = course.create_date,
            authors = if (course.author != null) listOf(course.author) else emptyList(),
            is_favorite = course.is_favorite,
        )
    }

    fun mapDomainToData(course: Course): CourseData {
        return CourseData(
            id = course.id,
            title = course.title,
            summary = course.summary,
            description = course.description,
            cover = course.cover,
            canonical_url = course.canonical_url,
            continue_url = course.continue_url,
            readiness = course.readiness,
            is_paid = course.is_paid,
            display_price = course.display_price,
            create_date = course.create_date,
            author = course.authors.getOrNull(0),
            is_favorite = course.is_favorite,
        )
    }

    fun mapDataToDomain(courses: Collection<CourseData>): List<Course> {
        return courses.map { course -> mapDataToDomain(course) }
    }

    fun mapDomainToData(courses: Collection<Course>): List<CourseData> {
        return courses.map { course -> mapDomainToData(course) }
    }
}