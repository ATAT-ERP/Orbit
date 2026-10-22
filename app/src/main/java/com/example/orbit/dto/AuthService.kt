package com.example.orbit.dto

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("users/login/")
    suspend fun login(@Body body: LoginRequestDto): Response<LoginResponseDto>

    @POST("users/logout/")
    suspend fun logout(): Response<Unit>
}