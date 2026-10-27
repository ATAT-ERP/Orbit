package com.example.orbit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.orbit.dto.NetworkModule
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
                    // Navegamos a HOME y destruimos LOGIN del historial
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Rutas protegidas
        composable(Routes.HOME) {
            HomeScreen(
                onLogout = {
                    onLogout()
                    // Navegamos a LOGIN y limpiamos TODO el historial
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}