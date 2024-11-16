package com.github.antonkonyshev.stepic.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.antonkonyshev.stepic.domain.model.Author
import com.github.antonkonyshev.stepic.domain.model.Course

@TypeConverters(DateConverter::class)
@Database(entities = [CourseData::class, AuthorData::class], version = 1, exportSchema = false)
abstract class StepicDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun authorDao(): AuthorDao
}