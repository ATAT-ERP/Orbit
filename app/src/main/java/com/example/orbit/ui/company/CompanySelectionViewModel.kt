package com.example.orbit.ui.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orbit.dto.ActiveCompany
import com.example.orbit.dto.CompanyDto
import com.example.orbit.dto.CompanyRepository
import com.example.orbit.dto.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CompanySelectionUiState {
    data object Loading : CompanySelectionUiState
    data class Content(val companies: List<CompanyDto>) : CompanySelectionUiState
    data object Empty : CompanySelectionUiState
    data class Error(val message: String) : CompanySelectionUiState
}

class CompanySelectionViewModel(private val companyRepository: CompanyRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<CompanySelectionUiState>(CompanySelectionUiState.Loading)
    val uiState: StateFlow<CompanySelectionUiState> = _uiState.asStateFlow()

    init {
        loadCompanies()
    }

    fun loadCompanies() {
        _uiState.value = CompanySelectionUiState.Loading
        viewModelScope.launch {
            when (val result = companyRepository.getCompanies()) {
                is NetworkResult.Success -> {
                    _uiState.value = if (result.data.isEmpty()) {
                        CompanySelectionUiState.Empty
                    } else {
                        CompanySelectionUiState.Content(result.data)
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = CompanySelectionUiState.Error(result.message)
                }
                is NetworkResult.Exception -> {
                    _uiState.value = CompanySelectionUiState.Error(
                        "No se pudo conectar. Revisá tu conexión e intentá de nuevo."
                    )
                }
            }
        }
    }

    fun selectCompany(company: CompanyDto) {
        ActiveCompany.select(company)
    }
}