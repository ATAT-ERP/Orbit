package com.example.orbit.dto

class CloudRepository(private val cloudService: CloudService) {

    suspend fun getFiles(companyId: String): NetworkResult<List<CloudFileDto>> {
        return safeApiCall { cloudService.getFiles(companyId) }
    }

    suspend fun getDownloadUrl(fileId: String): NetworkResult<DownloadUrlDto> {
        return safeApiCall { cloudService.getDownloadUrl(fileId) }
    }
}