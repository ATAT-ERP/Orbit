package com.example.orbit.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorDto(
    val code: String,
    val message: String,
    val errors: Map<String, List<String>>? = null
)

