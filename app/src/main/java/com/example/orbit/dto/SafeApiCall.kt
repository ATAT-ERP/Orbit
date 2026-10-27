package com.example.orbit.dto

import retrofit2.Response
import kotlinx.serialization.decodeFromString

suspend fun <T> safeApiCall(call: suspend () -> Response<T>): NetworkResult<T> {
    return try {
        val response = call()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            NetworkResult.Success(body)
        } else {
            val errorBodyText = response.errorBody()?.string()
            val errorDto: ApiErrorDto? = errorBodyText?.let { text ->
                try {
                    NexusBackClient.jsonConfig.decodeFromString<ApiErrorDto>(text)
                } catch (e: Exception) {
                    null
                }
            }
            NetworkResult.Error(
                code = errorDto?.code ?: "UNKNOWN_ERROR",
                message = errorDto?.message ?: "Error inesperado",
                httpCode = response.code()
            )
        }
    } catch (e: Exception) {
        NetworkResult.Exception(e)
    }
}