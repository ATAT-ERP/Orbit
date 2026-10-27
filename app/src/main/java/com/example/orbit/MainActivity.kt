package com.example.orbit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.orbit.auth.SupabaseClient
import com.example.orbit.dto.ActiveCompany
import com.example.orbit.dto.NetworkModule
import com.example.orbit.ui.navigation.AppNavigation
import com.example.orbit.ui.navigation.Routes
import com.example.orbit.ui.theme.OrbitTheme
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OrbitTheme {
                val sessionStatus by SupabaseClient.client.auth.sessionStatus.collectAsState()
                val coroutineScope = rememberCoroutineScope()
                val navController = rememberNavController()

                if (sessionStatus is SessionStatus.LoadingFromStorage) {
                    // Cumple la regla: "Evitar mostrar brevemente la pantalla de login"
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    val startDestination = when {
                        sessionStatus !is SessionStatus.Authenticated -> Routes.LOGIN
                        ActiveCompany.current.value == null -> Routes.COMPANY_SELECTION
                        else -> Routes.HOME
                    }
                    AppNavigation(
                        navController = navController,
                        startDestination = startDestination,
                        onLogout = {
                            coroutineScope.launch {
                                NetworkModule.authRepository.logout()
                            }
                            ActiveCompany.clear()
                        }
                    )
                }
            }
        }
    }
}