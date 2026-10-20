package com.example.orbit.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.orbit.ui.theme.OrbitTheme

@Composable
fun HomeScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Text(
            text = "Hello Android!",
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    OrbitTheme {
        HomeScreen()
    }
}
