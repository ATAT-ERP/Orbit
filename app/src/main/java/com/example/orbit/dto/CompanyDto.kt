package com.example.orbit.dto

import kotlinx.serialization.Serializable

@Serializable
data class CompanyDto(
    val id: String,
    val name: String
)