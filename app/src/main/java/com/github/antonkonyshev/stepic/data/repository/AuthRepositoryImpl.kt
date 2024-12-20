package com.github.antonkonyshev.stepic.data.repository

import android.util.Log
import com.github.antonkonyshev.stepic.data.network.Credentials
import com.github.antonkonyshev.stepic.data.network.StepicApi
import com.github.antonkonyshev.stepic.domain.model.Author
import com.github.antonkonyshev.stepic.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.HttpException

class AuthRepositoryImpl() : AuthRepository, KoinComponent {
    private val api: StepicApi by inject()
    private val _authenticated = MutableStateFlow(false)
    override val authenticated = _authenticated.asStateFlow()
    private val _account: MutableStateFlow<Author?> = MutableStateFlow(null)
    override val account = _account.asStateFlow()

    override suspend fun login(email: String, password: String) {
        try {
            val response = api.login(Credentials(email, password))
            if (response.code() != 204) {
                throw HttpException(response)
            } else {
                _authenticated.value = true
                loadAccount()
            }
        } catch (err: Exception) {
            Log.e(TAG, "Error on authentication: ${err.toString()}")
        }
    }

    override suspend fun loadAccount() {
        try {
            val response = api.loadAccount(1)
            if (response.users.isEmpty()) {
                _account.value = null
            } else {
                _account.value = response.users[0]
            }
        } catch (err: Exception) {
            Log.e(TAG, "Error on account data loading: ${err.toString()}")
        }
    }

    companion object {
        const val TAG = "AuthRepositoryImpl"
    }
}