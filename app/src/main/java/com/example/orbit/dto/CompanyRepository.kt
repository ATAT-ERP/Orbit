package com.example.orbit.dto

class CompanyRepository(private val companyService: CompanyService) {

    suspend fun getCompanies(): NetworkResult<List<CompanyDto>> {
        return safeApiCall { companyService.getCompanies() }
    }
}