package com.example.orbit.dto

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val code: String, val message: String, val httpCode: Int) : NetworkResult<Nothing>()
    data class Exception(val e: Throwable) : NetworkResult<Nothing>()
}