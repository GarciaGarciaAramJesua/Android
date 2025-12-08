package com.example.wearable.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wearable.mobile.data.model.ActiveBreak
import com.example.wearable.mobile.viewmodel.ActiveBreakViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileActiveBreakScreen(
    viewModel: ActiveBreakViewModel,
    onBack: () -> Unit
) {
    val availableBreaks by viewModel.availableBreaks.collectAsStateWithLifecycle()
    val todayBreaks by viewModel.todayBreaks.collectAsStateWithLifecycle()
    var showExerciseDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🏃 Pausas Activas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Estadísticas del día
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Completadas hoy",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${todayBreaks.size}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (todayBreaks.isNotEmpty()) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Tiempo activo",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${todayBreaks.sumOf { it.breakType.durationMinutes }} min",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Selecciona una pausa activa",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableBreaks) { break_ ->
                    ActiveBreakCard(
                        activeBreak = break_,
                        onClick = {
                            viewModel.startBreak(break_)
                            showExerciseDialog = true
                        }
                    )
                }
            }
        }
    }
    
    if (showExerciseDialog) {
        ActiveBreakDialog(
            viewModel = viewModel,
            onDismiss = {
                showExerciseDialog = false
                viewModel.clearUiState()
            }
        )
    }
}

@Composable
fun ActiveBreakCard(
    activeBreak: ActiveBreak,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${activeBreak.icon} ${activeBreak.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${activeBreak.exercises.size} ejercicios • ${activeBreak.durationMinutes} min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ActiveBreakDialog(
    viewModel: ActiveBreakViewModel,
    onDismiss: () -> Unit
) {
    val currentBreak by viewModel.currentBreak.collectAsStateWithLifecycle()
    val currentExerciseIndex by viewModel.currentExerciseIndex.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(uiState) {
        if (uiState is com.example.wearable.mobile.viewmodel.ActiveBreakUiState.Completed) {
            kotlinx.coroutines.delay(2000)
            onDismiss()
        }
    }
    
    currentBreak?.let { break_ ->
        val exercise = break_.exercises[currentExerciseIndex]
        val progress = (currentExerciseIndex + 1).toFloat() / break_.exercises.size.toFloat()
        
        AlertDialog(
            onDismissRequest = { },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(break_.icon, style = MaterialTheme.typography.displaySmall)
                    Text(break_.name)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Ejercicio ${currentExerciseIndex + 1} de ${break_.exercises.size}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = exercise.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "${exercise.durationSeconds} segundos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.nextExercise() }) {
                    Text(
                        if (currentExerciseIndex < break_.exercises.size - 1)
                            "Siguiente"
                        else
                            "Finalizar"
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.skipBreak()
                    onDismiss()
                }) {
                    Text("Saltar")
                }
            }
        )
    }
}
