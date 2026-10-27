package com.example.orbit.dto

import com.example.orbit.auth.AuthRepository
import com.example.orbit.auth.SupabaseClient
import io.github.jan.supabase.gotrue.auth

object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    private val tokenProvider: () -> String? = {
        SupabaseClient.client.auth.currentAccessTokenOrNull()
    }

    private val retrofit by lazy {
        NexusBackClient.create(
            baseUrl = BASE_URL,
            tokenProvider = tokenProvider,
            isDebug = true
        )
    }

    private val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(userService)
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(authService)
    }

    private val companyService: CompanyService by lazy {
        retrofit.create(CompanyService::class.java)
    }

    val companyRepository: CompanyRepository by lazy {
        CompanyRepository(companyService)
    }
}