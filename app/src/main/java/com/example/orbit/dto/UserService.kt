package com.example.orbit.dto

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Body

interface UserService {
    @GET("users/me/")
    suspend fun getCurrentUser(): Response<UserDto>

    @PATCH("users/me/")
    suspend fun updateCurrentUser(@Body update: UserUpdateDto): Response<UserDto>
}