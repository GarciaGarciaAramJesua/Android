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
 * Pantalla de estadísticas diarias
 */
@Composable
fun StatsScreen(
    waterViewModel: WaterViewModel,
    activeBreakViewModel: ActiveBreakViewModel,
    onBack: () -> Unit
) {
    val waterStats by waterViewModel.dailyStats.collectAsStateWithLifecycle()
    val activeBreaks by activeBreakViewModel.todayBreaks.collectAsStateWithLifecycle()
    
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
                text = "📊 Estadísticas Hoy",
                style = MaterialTheme.typography.title2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Sección de Hidratación
        item {
            Card(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                enabled = false
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "💧 Hidratación",
                        style = MaterialTheme.typography.title3,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = "${waterStats.totalAmount}ml",
                        style = MaterialTheme.typography.display2,
                        color = MaterialTheme.colors.primary
                    )
                    
                    Text(
                        text = "Meta: ${waterStats.goal}ml",
                        style = MaterialTheme.typography.caption1
                    )
                    
                    Text(
                        text = "${(waterStats.progress * 100).toInt()}% completado",
                        style = MaterialTheme.typography.caption2,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    
                    if (waterStats.isGoalReached) {
                        Text(
                            text = "¡Meta alcanzada! 🎉",
                            style = MaterialTheme.typography.caption1,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
        
        // Sección de Pausas Activas
        item {
            Card(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                enabled = false
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🏃 Pausas Activas",
                        style = MaterialTheme.typography.title3,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = "${activeBreaks.size}",
                        style = MaterialTheme.typography.display2,
                        color = MaterialTheme.colors.primary
                    )
                    
                    Text(
                        text = "completadas hoy",
                        style = MaterialTheme.typography.caption1
                    )
                    
                    if (activeBreaks.isNotEmpty()) {
                        val totalMinutes = activeBreaks.sumOf { it.breakType.durationMinutes }
                        Text(
                            text = "$totalMinutes minutos activos",
                            style = MaterialTheme.typography.caption2,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
        
        // Resumen general
        item {
            Spacer(modifier = Modifier.height(8.dp))
            
            val totalActivities = waterStats.intakes.size + activeBreaks.size
            Text(
                text = "Total de actividades: $totalActivities",
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        item {
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Volver")
            }
        }
    }
}
