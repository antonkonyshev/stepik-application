package com.github.antonkonyshev.stepic.data.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface AuthorDao {
    @Upsert
    suspend fun upsertAuthor(author: AuthorData)

    @Query("select * from author where id = :id limit 1")
    suspend fun getAuthorById(id: Long): AuthorData?
}

@Entity(tableName = "author")
data class AuthorData(
    @PrimaryKey val id: Long,
    val full_name: String,
    val avatar: String,
)