package com.github.antonkonyshev.stepic.domain.repository

import com.github.antonkonyshev.stepic.domain.model.Author
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authenticated: StateFlow<Boolean>
    val account: StateFlow<Author?>
    suspend fun login(email: String, password: String)
    suspend fun loadAccount()
}