package com.example.wearable.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wearable.mobile.ui.screens.*
import com.example.wearable.mobile.ui.theme.WearableTheme
import com.example.wearable.mobile.viewmodel.ActiveBreakViewModel
import com.example.wearable.mobile.viewmodel.WaterViewModel

class MainActivity : ComponentActivity() {
    
    private val waterViewModel: WaterViewModel by viewModels()
    private val activeBreakViewModel: ActiveBreakViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            WearableTheme {
                MobileApp(
                    waterViewModel = waterViewModel,
                    activeBreakViewModel = activeBreakViewModel
                )
            }
        }
    }
}

@Composable
fun MobileApp(
    waterViewModel: WaterViewModel,
    activeBreakViewModel: ActiveBreakViewModel
) {
    val navController = rememberNavController()
    
    Scaffold { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                MobileHomeScreen(
                    onNavigateToWater = { navController.navigate("water") },
                    onNavigateToActiveBreak = { navController.navigate("active_break") },
                    onNavigateToStats = { navController.navigate("stats") },
                    onNavigateToSettings = { navController.navigate("settings") }
                )
            }
            
            composable("water") {
                MobileWaterScreen(
                    viewModel = waterViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("active_break") {
                MobileActiveBreakScreen(
                    viewModel = activeBreakViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("stats") {
                MobileStatsScreen(
                    waterViewModel = waterViewModel,
                    activeBreakViewModel = activeBreakViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("settings") {
                MobileSettingsScreen(
                    waterViewModel = waterViewModel,
                    activeBreakViewModel = activeBreakViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
