package com.example.orbit.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CloudFileDto(
    val id: String,
    val name: String,
    @SerialName("original_name") val originalName: String,
    @SerialName("mime_type") val mimeType: String,
    val size: Long,
    @SerialName("created_at") val createdAt: String
)