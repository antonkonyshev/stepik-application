package com.github.antonkonyshev.stepic.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authenticated: StateFlow<Boolean>
    suspend fun login(email: String, password: String)
}