package com.github.antonkonyshev.stepic.domain.repository

import com.github.antonkonyshev.stepic.domain.model.Author

interface AuthorRepository {
    suspend fun getAuthorById(authorId: Long): Author?
}