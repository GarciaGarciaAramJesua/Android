package com.example.wearable.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.wearable.presentation.screens.*
import com.example.wearable.presentation.theme.WearableTheme
import com.example.wearable.presentation.viewmodel.ActiveBreakViewModel
import com.example.wearable.presentation.viewmodel.WaterViewModel

class MainActivity : ComponentActivity() {
    
    private val waterViewModel: WaterViewModel by viewModels()
    private val activeBreakViewModel: ActiveBreakViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp(
                waterViewModel = waterViewModel,
                activeBreakViewModel = activeBreakViewModel
            )
        }
    }
}

@Composable
fun WearApp(
    waterViewModel: WaterViewModel,
    activeBreakViewModel: ActiveBreakViewModel
) {
    WearableTheme {
        val navController = rememberSwipeDismissableNavController()
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            WearNavHost(
                navController = navController,
                waterViewModel = waterViewModel,
                activeBreakViewModel = activeBreakViewModel
            )
        }
    }
}

@Composable
fun WearNavHost(
    navController: NavHostController,
    waterViewModel: WaterViewModel,
    activeBreakViewModel: ActiveBreakViewModel
) {
    SwipeDismissableNavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToWater = { navController.navigate("water") },
                onNavigateToActiveBreak = { navController.navigate("active_break_list") },
                onNavigateToStats = { navController.navigate("stats") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        
        composable("water") {
            WaterScreen(
                viewModel = waterViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("active_break_list") {
            ActiveBreakListScreen(
                viewModel = activeBreakViewModel,
                onBreakSelected = { navController.navigate("active_break_exercise") },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("active_break_exercise") {
            ActiveBreakExerciseScreen(
                viewModel = activeBreakViewModel,
                onComplete = { navController.popBackStack() }
            )
        }
        
        composable("stats") {
            StatsScreen(
                waterViewModel = waterViewModel,
                activeBreakViewModel = activeBreakViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("settings") {
            SettingsScreen(
                waterViewModel = waterViewModel,
                activeBreakViewModel = activeBreakViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}