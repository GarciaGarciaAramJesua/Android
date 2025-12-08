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
import com.example.wearable.presentation.viewmodel.ActiveBreakViewModel
import com.example.wearable.presentation.viewmodel.ActiveBreakUiState

/**
 * Pantalla de ejecución de pausa activa
 */
@Composable
fun ActiveBreakExerciseScreen(
    viewModel: ActiveBreakViewModel,
    onComplete: () -> Unit
) {
    val currentBreak by viewModel.currentBreak.collectAsStateWithLifecycle()
    val currentExerciseIndex by viewModel.currentExerciseIndex.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Navegar cuando se complete
    LaunchedEffect(uiState) {
        if (uiState is ActiveBreakUiState.Completed) {
            kotlinx.coroutines.delay(2000)
            onComplete()
        }
    }
    
    currentBreak?.let { break_ ->
        val exercise = break_.exercises[currentExerciseIndex]
        val progress = (currentExerciseIndex + 1).toFloat() / break_.exercises.size.toFloat()
        
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
                    text = break_.icon,
                    style = MaterialTheme.typography.display1,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            item {
                Text(
                    text = break_.name,
                    style = MaterialTheme.typography.title3,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            
            item {
                Text(
                    text = "Ejercicio ${currentExerciseIndex + 1} de ${break_.exercises.size}",
                    style = MaterialTheme.typography.caption1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            item {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(8.dp),
                    strokeWidth = 6.dp
                )
            }
            
            item {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.title3,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item {
                Text(
                    text = exercise.description,
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            item {
                Text(
                    text = "${exercise.durationSeconds} segundos",
                    style = MaterialTheme.typography.caption1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (currentExerciseIndex > 0) {
                        CompactChip(
                            onClick = { viewModel.previousExercise() },
                            label = { Text("◀ Anterior") },
                            modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                        )
                    }
                    
                    CompactChip(
                        onClick = { viewModel.nextExercise() },
                        label = { 
                            Text(
                                if (currentExerciseIndex < break_.exercises.size - 1) 
                                    "Siguiente ▶" 
                                else 
                                    "Finalizar ✓"
                            )
                        },
                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                        colors = ChipDefaults.primaryChipColors()
                    )
                }
            }
            
            item {
                Button(
                    onClick = { 
                        viewModel.skipBreak()
                        onComplete()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.secondaryButtonColors()
                ) {
                    Text("Saltar")
                }
            }
            
            // Mostrar mensaje de completado
            if (uiState is ActiveBreakUiState.Completed) {
                item {
                    Text(
                        text = (uiState as ActiveBreakUiState.Completed).message,
                        style = MaterialTheme.typography.title3,
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
