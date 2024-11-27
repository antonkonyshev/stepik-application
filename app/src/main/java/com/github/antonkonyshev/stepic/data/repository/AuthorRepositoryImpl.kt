package com.github.antonkonyshev.stepic.data.repository

import android.util.Log
import com.github.antonkonyshev.stepic.data.database.AuthorDao
import com.github.antonkonyshev.stepic.data.database.AuthorMapper
import com.github.antonkonyshev.stepic.data.network.StepicApi
import com.github.antonkonyshev.stepic.domain.model.Author
import com.github.antonkonyshev.stepic.domain.repository.AuthorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthorRepositoryImpl() : AuthorRepository, KoinComponent {
    private val api: StepicApi by inject()
    private val authorDao: AuthorDao by inject()

    override suspend fun getAuthorById(authorId: Long): Author? {
        try {
            val authorData = authorDao.getAuthorById(authorId)
            if (authorData != null) {
                return AuthorMapper.mapDataToDomain(authorData)
            }
        } catch (err: Exception) {
            Log.e(TAG, "Error on author data loading: ${err.toString()}")
        }

        try {
            return api.fetchUser(authorId).users.let { authors ->
                if (authors.isNotEmpty()) {
                    try {
                        coroutineScope {
                            launch(Dispatchers.IO) {
                                authorDao.upsertAuthor(AuthorMapper.mapDomainToData(authors[0]))
                            }
                        }
                    } catch (err: Exception) {
                        Log.e(TAG, "Error on author data caching: ${err.toString()}")
                    }
                    return@let authors[0]
                }
                return@let null
            }
        } catch (err: Exception) {
            Log.e(TAG, "Error on author data fetching: ${err.toString()}")
        }
        return null
    }

    companion object {
        const val TAG = "AuthorRepositoryImpl"
    }
}