package com.example.orbit.dto

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CloudService {
    @GET("cloud/files/")
    suspend fun getFiles(@Query("company_id") companyId: String): Response<List<CloudFileDto>>

    @GET("cloud/files/{id}/download/")
    suspend fun getDownloadUrl(@Path("id") fileId: String): Response<DownloadUrlDto>
}