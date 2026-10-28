package com.example.orbit.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orbit.dto.NetworkResult
import com.example.orbit.dto.UserDto
import com.example.orbit.dto.UserRepository
import com.example.orbit.dto.UserUpdateDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val NAME_MAX_LENGTH = 150
private const val CONNECTION_ERROR = "No se pudo conectar. Revisá tu conexión e intentá de nuevo."

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Error(val message: String) : ProfileUiState
    data class Content(
        val user: UserDto,
        val firstName: String,
        val lastName: String,
        val firstNameError: String? = null,
        val lastNameError: String? = null,
        val isSaving: Boolean = false,
        val saveError: String? = null
    ) : ProfileUiState {
        val hasChanges: Boolean
            get() = firstName.trim() != user.firstName || lastName.trim() != user.lastName
    }
}

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            _uiState.value = when (val result = userRepository.getCurrentUser()) {
                is NetworkResult.Success -> contentFor(result.data)
                is NetworkResult.Error -> ProfileUiState.Error(result.message)
                is NetworkResult.Exception -> ProfileUiState.Error(CONNECTION_ERROR)
            }
        }
    }

    fun onFirstNameChange(value: String) {
        updateContent { it.copy(firstName = value, firstNameError = null) }
    }

    fun onLastNameChange(value: String) {
        updateContent { it.copy(lastName = value, lastNameError = null) }
    }

    fun discardChanges() {
        updateContent { contentFor(it.user) }
    }

    fun saveProfile() {
        val current = _uiState.value as? ProfileUiState.Content ?: return
        if (current.isSaving) return

        val firstName = current.firstName.trim()
        val lastName = current.lastName.trim()
        val firstNameError = validateName(firstName)
        val lastNameError = validateName(lastName)
        if (firstNameError != null || lastNameError != null) {
            _uiState.value = current.copy(
                firstNameError = firstNameError,
                lastNameError = lastNameError
            )
            return
        }

        _uiState.value = current.copy(isSaving = true, saveError = null)
        viewModelScope.launch {
            when (val result = userRepository.updateCurrentUser(UserUpdateDto(firstName, lastName))) {
                is NetworkResult.Success -> _uiState.value = contentFor(result.data)
                is NetworkResult.Error -> updateContent {
                    it.copy(isSaving = false, saveError = result.message)
                }
                is NetworkResult.Exception -> updateContent {
                    it.copy(isSaving = false, saveError = CONNECTION_ERROR)
                }
            }
        }
    }

    private fun validateName(value: String): String? = when {
        value.isEmpty() -> "Este campo es obligatorio."
        value.length > NAME_MAX_LENGTH -> "Máximo $NAME_MAX_LENGTH caracteres."
        else -> null
    }

    private fun contentFor(user: UserDto) = ProfileUiState.Content(
        user = user,
        firstName = user.firstName,
        lastName = user.lastName
    )

    private fun updateContent(transform: (ProfileUiState.Content) -> ProfileUiState.Content) {
        val current = _uiState.value as? ProfileUiState.Content ?: return
        _uiState.value = transform(current)
    }
}