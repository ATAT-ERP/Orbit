package com.example.orbit.dto

import retrofit2.Response
import retrofit2.http.GET

interface CompanyService {
    @GET("companies/")
    suspend fun getCompanies(): Response<List<CompanyDto>>
}