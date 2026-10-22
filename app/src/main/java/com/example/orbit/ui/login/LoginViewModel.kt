package com.example.orbit.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orbit.auth.AuthRepository
import com.example.orbit.dto.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data object Success : LoginUiState
    data class Error(val message: String) : LoginUiState
}

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (_uiState.value is LoginUiState.Loading) return

        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            when (val result = authRepository.login(email, password)) {
                is NetworkResult.Success -> {
                    _uiState.value = LoginUiState.Success
                }
                is NetworkResult.Error -> {
                    _uiState.value = LoginUiState.Error(result.message)
                }
                is NetworkResult.Exception -> {
                    _uiState.value = LoginUiState.Error("No se pudo conectar. Revisá tu conexión e intentá de nuevo.")
                }
            }
        }
    }
}