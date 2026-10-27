package com.example.orbit.auth

import com.example.orbit.dto.ApiErrorDto
import com.example.orbit.dto.AuthService
import com.example.orbit.dto.LoginRequestDto
import com.example.orbit.dto.NetworkResult
import com.example.orbit.dto.NexusBackClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.user.UserSession
import kotlinx.serialization.decodeFromString

class AuthRepository(private val authService: AuthService) {

    suspend fun login(email: String, password: String): NetworkResult<Unit> {
        return try {
            val response = authService.login(LoginRequestDto(email, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    // Usamos importAuthToken. El SDK toma los tokens, contacta a Supabase
                    // para descargar el usuario faltante, y guarda la sesión completa en disco.
                    SupabaseClient.client.auth.importAuthToken(
                        accessToken = body.accessToken,
                        refreshToken = body.refreshToken,
                        autoRefresh = true
                    )
                    NetworkResult.Success(Unit)
                } else {
                    NetworkResult.Error("NEX-000-000", "Respuesta vacía", response.code())
                }
            } else {
                val errorText = response.errorBody()?.string()
                val parsed = errorText?.let {
                    NexusBackClient.jsonConfig.decodeFromString<ApiErrorDto>(it)
                }
                NetworkResult.Error(
                    parsed?.code ?: "NEX-000-000",
                    parsed?.message ?: "Error desconocido",
                    response.code()
                )
            }
        } catch (e: Throwable) {
            NetworkResult.Exception(e)
        }
    }

    suspend fun logout(): NetworkResult<Unit> {
        val remoteResult: NetworkResult<Unit> = try {
            val response = authService.logout()
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                val errorText = response.errorBody()?.string()
                val parsed = errorText?.let {
                    NexusBackClient.jsonConfig.decodeFromString<ApiErrorDto>(it)
                }
                NetworkResult.Error(
                    parsed?.code ?: "NEX-000-000",
                    parsed?.message ?: "Error desconocido",
                    response.code()
                )
            }
        } catch (e: Throwable) {
            NetworkResult.Exception(e)
        }

        SupabaseClient.client.auth.clearSession()

        return remoteResult
    }
}