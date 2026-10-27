package com.example.orbit.dto

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ActiveCompany {
    private val _current = MutableStateFlow<CompanyDto?>(null)
    val current: StateFlow<CompanyDto?> = _current.asStateFlow()

    fun select(company: CompanyDto) {
        _current.value = company
    }

    fun clear() {
        _current.value = null
    }
}