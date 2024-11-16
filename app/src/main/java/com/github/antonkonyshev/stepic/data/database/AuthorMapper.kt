package com.github.antonkonyshev.stepic.data.database

import com.github.antonkonyshev.stepic.domain.model.Author

object AuthorMapper {
    fun mapDataToDomain(author: AuthorData): Author {
        return Author(
            id = author.id,
            full_name = author.full_name,
            avatar = author.avatar
        )
    }

    fun mapDomainToData(author: Author): AuthorData {
        return AuthorData(
            id = author.id,
            full_name = author.full_name,
            avatar = author.avatar
        )
    }

    fun mapDataToDomain(authors: Collection<AuthorData>): List<Author> {
        return authors.map { author -> mapDataToDomain(author) }
    }

    fun mapDomainToData(authors: Collection<Author>): List<AuthorData> {
        return authors.map { author -> mapDomainToData(author) }
    }
}