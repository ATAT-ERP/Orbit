package com.example.orbit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.orbit.dto.NetworkModule
import com.example.orbit.ui.home.HomeScreen
import com.example.orbit.ui.login.LoginScreen
import com.example.orbit.ui.login.LoginViewModel
import com.example.orbit.ui.theme.OrbitTheme
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OrbitTheme {
                var isLoggedIn by remember {
                    mutableStateOf(
                        com.example.orbit.auth.SupabaseClient.client.auth.currentAccessTokenOrNull() != null
                    )
                }
                val coroutineScope = rememberCoroutineScope()

                if (isLoggedIn) {
                    HomeScreen(
                        onLogout = {
                            coroutineScope.launch {
                                NetworkModule.authRepository.logout()
                                isLoggedIn = false
                            }
                        }
                    )
                } else {
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
                        onLoginSuccess = { isLoggedIn = true }
                    )
                }
            }
        }
    }
}
