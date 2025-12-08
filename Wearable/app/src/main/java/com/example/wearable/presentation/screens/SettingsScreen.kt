package com.example.wearable.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.*
import com.example.wearable.presentation.viewmodel.WaterViewModel
import com.example.wearable.presentation.viewmodel.ActiveBreakViewModel

/**
 * Pantalla de configuración
 */
@Composable
fun SettingsScreen(
    waterViewModel: WaterViewModel,
    activeBreakViewModel: ActiveBreakViewModel,
    onBack: () -> Unit
) {
    val waterSettings by waterViewModel.reminderSettings.collectAsStateWithLifecycle()
    val breakSettings by activeBreakViewModel.reminderSettings.collectAsStateWithLifecycle()
    
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 32.dp,
            start = 10.dp,
            end = 10.dp,
            bottom = 40.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "⚙️ Configuración",
                style = MaterialTheme.typography.title2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Configuración de Hidratación
        item {
            Text(
                text = "💧 Recordatorios de Agua",
                style = MaterialTheme.typography.title3,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        item {
            Chip(
                onClick = {
                    waterViewModel.updateReminderSettings(
                        waterSettings.copy(enabled = !waterSettings.enabled)
                    )
                },
                label = {
                    Text("Recordatorios de Agua: ${if (waterSettings.enabled) "✓" else "✗"}")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = if (waterSettings.enabled) {
                    ChipDefaults.primaryChipColors()
                } else {
                    ChipDefaults.secondaryChipColors()
                }
            )
        }
        
        item {
            Chip(
                onClick = { },
                label = {
                    Column {
                        Text("Intervalo: ${waterSettings.intervalMinutes} min")
                        Text(
                            "Meta diaria: ${waterSettings.dailyGoal}ml",
                            style = MaterialTheme.typography.caption2
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ChipDefaults.secondaryChipColors(),
                enabled = false
            )
        }
        
        // Configuración de Pausas Activas
        item {
            Text(
                text = "🏃 Recordatorios de Pausas",
                style = MaterialTheme.typography.title3,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }
        
        item {
            Chip(
                onClick = {
                    activeBreakViewModel.updateReminderSettings(
                        breakSettings.copy(enabled = !breakSettings.enabled)
                    )
                },
                label = {
                    Text("Recordatorios de Pausas: ${if (breakSettings.enabled) "✓" else "✗"}")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = if (breakSettings.enabled) {
                    ChipDefaults.primaryChipColors()
                } else {
                    ChipDefaults.secondaryChipColors()
                }
            )
        }
        
        item {
            Chip(
                onClick = { },
                label = {
                    Text("Intervalo: ${breakSettings.intervalMinutes} min")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ChipDefaults.secondaryChipColors(),
                enabled = false
            )
        }
        
        // Información de la app
        item {
            Text(
                text = "Salud Activa v1.0",
                style = MaterialTheme.typography.caption2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }
        
        item {
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}
