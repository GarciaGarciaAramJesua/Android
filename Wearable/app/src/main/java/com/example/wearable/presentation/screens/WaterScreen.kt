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

/**
 * Pantalla de hidratación con seguimiento de agua
 */
@Composable
fun WaterScreen(
    viewModel: WaterViewModel,
    onBack: () -> Unit
) {
    val dailyStats by viewModel.dailyStats.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Mostrar mensajes de éxito/error
    LaunchedEffect(uiState) {
        if (uiState is com.example.wearable.presentation.viewmodel.WaterUiState.Success ||
            uiState is com.example.wearable.presentation.viewmodel.WaterUiState.Error) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearUiState()
        }
    }
    
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
                text = "💧 Hidratación",
                style = MaterialTheme.typography.title2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        item {
            // Progreso circular
            CircularProgressIndicator(
                progress = dailyStats.progress,
                modifier = Modifier
                    .size(120.dp)
                    .padding(16.dp),
                strokeWidth = 8.dp
            )
        }
        
        item {
            Text(
                text = "${dailyStats.totalAmount}ml / ${dailyStats.goal}ml",
                style = MaterialTheme.typography.title3,
                textAlign = TextAlign.Center
            )
        }
        
        item {
            Text(
                text = if (dailyStats.isGoalReached) "¡Meta alcanzada! 🎉" else "Sigue hidratándote",
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        // Botones de cantidad rápida
        item {
            Text(
                text = "Agregar agua",
                style = MaterialTheme.typography.caption1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CompactChip(
                    onClick = { viewModel.addWater(100) },
                    label = { Text("100ml") },
                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                )
                CompactChip(
                    onClick = { viewModel.addWater(250) },
                    label = { Text("250ml") },
                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                )
            }
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CompactChip(
                    onClick = { viewModel.addWater(500) },
                    label = { Text("500ml") },
                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                )
                CompactChip(
                    onClick = { viewModel.addWater(1000) },
                    label = { Text("1L") },
                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                )
            }
        }
        
        // Mostrar estado
        when (val state = uiState) {
            is com.example.wearable.presentation.viewmodel.WaterUiState.Success -> {
                item {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.caption1,
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            is com.example.wearable.presentation.viewmodel.WaterUiState.Error -> {
                item {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.caption1,
                        color = MaterialTheme.colors.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            else -> {}
        }
        
        item {
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Volver")
            }
        }
    }
}
