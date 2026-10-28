package com.example.orbit.dto

class UserRepository(private val userService: UserService) {

    suspend fun getCurrentUser(): NetworkResult<UserDto> {
        return safeApiCall { userService.getCurrentUser() }
    }

    suspend fun updateCurrentUser(update: UserUpdateDto): NetworkResult<UserDto> {
        return safeApiCall { userService.updateCurrentUser(update) }
    }
}