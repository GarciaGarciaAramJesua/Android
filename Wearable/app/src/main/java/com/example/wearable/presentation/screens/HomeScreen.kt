package com.example.wearable.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.*

/**
 * Pantalla principal con navegación a las diferentes secciones
 */
@Composable
fun HomeScreen(
    onNavigateToWater: () -> Unit,
    onNavigateToActiveBreak: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 32.dp,
            start = 10.dp,
            end = 10.dp,
            bottom = 40.dp
        ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Salud Activa",
                style = MaterialTheme.typography.title2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        item {
            Chip(
                onClick = onNavigateToWater,
                label = {
                    Text(
                        text = "💧 Hidratación",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = ChipDefaults.primaryChipColors()
            )
        }
        
        item {
            Chip(
                onClick = onNavigateToActiveBreak,
                label = {
                    Text(
                        text = "🏃 Pausas Activas",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = ChipDefaults.primaryChipColors()
            )
        }
        
        item {
            Chip(
                onClick = onNavigateToStats,
                label = {
                    Text(
                        text = "📊 Estadísticas",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = ChipDefaults.secondaryChipColors()
            )
        }
        
        item {
            Chip(
                onClick = onNavigateToSettings,
                label = {
                    Text(
                        text = "⚙️ Configuración",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = ChipDefaults.secondaryChipColors()
            )
        }
    }
}
