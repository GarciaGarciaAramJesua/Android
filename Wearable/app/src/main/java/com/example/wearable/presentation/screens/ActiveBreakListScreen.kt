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
import com.example.wearable.data.model.ActiveBreak
import com.example.wearable.presentation.viewmodel.ActiveBreakViewModel

/**
 * Pantalla de selección de pausas activas
 */
@Composable
fun ActiveBreakListScreen(
    viewModel: ActiveBreakViewModel,
    onBreakSelected: () -> Unit,
    onBack: () -> Unit
) {
    val availableBreaks by viewModel.availableBreaks.collectAsStateWithLifecycle()
    val todayBreaks by viewModel.todayBreaks.collectAsStateWithLifecycle()
    
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
                text = "🏃 Pausas Activas",
                style = MaterialTheme.typography.title2,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        item {
            Text(
                text = "Completadas hoy: ${todayBreaks.size}",
                style = MaterialTheme.typography.caption1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        items(availableBreaks.size) { index ->
            val break_ = availableBreaks[index]
            ActiveBreakCard(
                activeBreak = break_,
                onClick = {
                    viewModel.startBreak(break_)
                    onBreakSelected()
                }
            )
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

@Composable
fun ActiveBreakCard(
    activeBreak: ActiveBreak,
    onClick: () -> Unit
) {
    Chip(
        onClick = onClick,
        label = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "${activeBreak.icon} ${activeBreak.name}",
                    style = MaterialTheme.typography.button
                )
                Text(
                    text = "${activeBreak.exercises.size} ejercicios • ${activeBreak.durationMinutes} min",
                    style = MaterialTheme.typography.caption2
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ChipDefaults.primaryChipColors()
    )
}
