package com.example.orbit.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val email: String?,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("avatar_path") val avatarPath: String?,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("is_system_admin") val isSystemAdmin: Boolean
)

@Serializable
data class UserUpdateDto(
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String
)