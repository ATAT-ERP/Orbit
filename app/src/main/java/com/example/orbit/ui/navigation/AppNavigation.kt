package com.example.orbit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.orbit.dto.NetworkModule
import com.example.orbit.ui.company.CompanySelectionScreen
import com.example.orbit.ui.company.CompanySelectionViewModel
import com.example.orbit.ui.home.HomeScreen
import com.example.orbit.ui.login.LoginScreen
import com.example.orbit.ui.login.LoginViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Rutas públicas
        composable(Routes.LOGIN) {
            val loginViewModel: LoginViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return LoginViewModel(NetworkModule.authRepository) as T
                    }
                }
            )
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    // Tras loguearse, va a elegir empresa (no directo a Home)
                    navController.navigate(Routes.COMPANY_SELECTION) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Rutas protegidas
        composable(Routes.COMPANY_SELECTION) {
            val companySelectionViewModel: CompanySelectionViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return CompanySelectionViewModel(NetworkModule.companyRepository) as T
                    }
                }
            )
            CompanySelectionScreen(
                viewModel = companySelectionViewModel,
                onCompanySelected = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.COMPANY_SELECTION) { inclusive = true }
                    }
                },
                onLogout = {
                    onLogout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onLogout = {
                    onLogout()
                    // Navegamos a LOGIN y limpiamos TODO el historial
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                },
                onChangeCompany = {
                    // Volvemos a elegir empresa, sacando Home del historial
                    navController.navigate(Routes.COMPANY_SELECTION) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}