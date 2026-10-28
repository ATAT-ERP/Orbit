package com.example.orbit.ui.company

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.orbit.dto.CompanyDto

@Composable
fun CompanySelectionScreen(
    viewModel: CompanySelectionViewModel,
    onCompanySelected: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is CompanySelectionUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is CompanySelectionUiState.Empty -> {
                    Text("No tenés empresas asignadas todavía.")
                }

                is CompanySelectionUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.loadCompanies() }) {
                            Text("Reintentar")
                        }
                    }
                }

                is CompanySelectionUiState.Content -> {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(state.companies) { company: CompanyDto ->
                            ListItem(
                                headlineContent = { Text(company.name) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.selectCompany(company)
                                        onCompanySelected()
                                    }
                                    .padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}