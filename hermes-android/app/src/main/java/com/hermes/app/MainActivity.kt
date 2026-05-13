package com.hermes.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.hermes.presentation.ui.theme.HermesColorScheme
import com.hermes.presentation.ui.theme.HermesTypography
import com.hermes.presentation.ui.navigation.HermesNavigation
import com.hermes.presentation.ui.screen.WelcomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            var showWelcome by remember { mutableStateOf(true) }

            MaterialTheme(
                colorScheme = HermesColorScheme,
                typography = HermesTypography
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showWelcome) {
                        WelcomeScreen(
                            onStartClick = { showWelcome = false }
                        )
                    } else {
                        HermesNavigation(navController = navController)
                    }
                }
            }
        }
    }
}