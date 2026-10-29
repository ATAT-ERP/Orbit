package com.example.orbit.ui.cloud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orbit.dto.ActiveCompany
import com.example.orbit.dto.CloudFileDto
import com.example.orbit.dto.CloudRepository
import com.example.orbit.dto.NetworkResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

sealed interface CloudUiState {
    data object Loading : CloudUiState
    data class Content(val files: List<CloudFileDto>) : CloudUiState
    data object Empty : CloudUiState
    data class Error(val message: String) : CloudUiState
}

sealed interface CloudDownload {
    data class Ready(val url: String, val fileName: String) : CloudDownload
    data class Failed(val message: String) : CloudDownload
}

class CloudViewModel(private val cloudRepository: CloudRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<CloudUiState>(CloudUiState.Loading)
    val uiState: StateFlow<CloudUiState> = _uiState.asStateFlow()

    private val _download = MutableSharedFlow<CloudDownload>(extraBufferCapacity = 1)
    val download: SharedFlow<CloudDownload> = _download.asSharedFlow()

    init {
        viewModelScope.launch {
            ActiveCompany.current.filterNotNull().collect { company ->
                loadFiles(company.id)
            }
        }
    }

    fun retry() {
        val company = ActiveCompany.current.value ?: return
        loadFiles(company.id)
    }

    fun downloadFile(file: CloudFileDto) {
        viewModelScope.launch {
            _download.emit(
                when (val result = cloudRepository.getDownloadUrl(file.id)) {
                    is NetworkResult.Success -> CloudDownload.Ready(
                        url = result.data.url,
                        fileName = file.originalName
                    )
                    is NetworkResult.Error -> CloudDownload.Failed(result.message)
                    is NetworkResult.Exception -> CloudDownload.Failed(
                        "No se pudo conectar. Revisá tu conexión e intentá de nuevo."
                    )
                }
            )
        }
    }

    private fun loadFiles(companyId: String) {
        _uiState.value = CloudUiState.Loading
        viewModelScope.launch {
            when (val result = cloudRepository.getFiles(companyId)) {
                is NetworkResult.Success -> {
                    _uiState.value = if (result.data.isEmpty()) {
                        CloudUiState.Empty
                    } else {
                        CloudUiState.Content(result.data)
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = CloudUiState.Error(
                        if (result.code == "NEX-DOC-002") {
                            "No tenés acceso a los archivos de esta empresa."
                        } else {
                            result.message
                        }
                    )
                }
                is NetworkResult.Exception -> {
                    _uiState.value = CloudUiState.Error(
                        "No se pudo conectar. Revisá tu conexión e intentá de nuevo."
                    )
                }
            }
        }
    }
}