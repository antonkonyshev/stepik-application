package com.github.antonkonyshev.stepic

import com.github.antonkonyshev.stepic.data.database.AuthorData
import com.github.antonkonyshev.stepic.data.database.AuthorMapper
import com.github.antonkonyshev.stepic.domain.model.Author
import org.junit.Test
import kotlin.test.assertEquals

class AuthorMapperTestCase {
    val author = Author(12, "Testing Test", "https://localhost/avatar.svg")

    @Test
    fun testToData() {
        assertEquals(
            AuthorData(author.id, author.full_name, author.avatar),
            AuthorMapper.mapDomainToData(author)
        )
    }

    @Test
    fun testToDomain() {
        assertEquals(
            author,
            AuthorMapper.mapDataToDomain(
                AuthorData(author.id, author.full_name, author.avatar),
            )
        )
    }

    @Test
    fun testCollectionToData() {
        assertEquals(
            listOf(AuthorData(author.id, author.full_name, author.avatar)),
            listOf(AuthorMapper.mapDomainToData(author)),
        )
    }

    @Test
    fun testCollectionToDomain() {
        assertEquals(
            listOf(author),
            listOf(AuthorMapper.mapDataToDomain(
                AuthorData(author.id, author.full_name, author.avatar),
            ))
        )
    }
}