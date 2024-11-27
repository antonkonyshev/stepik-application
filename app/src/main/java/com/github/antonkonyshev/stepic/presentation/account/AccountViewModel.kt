package com.github.antonkonyshev.stepic.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.antonkonyshev.stepic.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AccountViewModel() : ViewModel(), KoinComponent {
    val authRepository: AuthRepository by inject()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun authenticate(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            authRepository.login(email, password)
            _loading.value = false
        }
    }
}