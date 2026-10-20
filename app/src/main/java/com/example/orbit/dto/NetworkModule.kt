package com.example.orbit.dto

object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    private var tokenProvider: () -> String? = { null }

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
}