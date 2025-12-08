package com.example.wearable.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wearable.mobile.viewmodel.WaterViewModel
import com.example.wearable.mobile.viewmodel.WaterUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileWaterScreen(
    viewModel: WaterViewModel,
    onBack: () -> Unit
) {
    val dailyStats by viewModel.dailyStats.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(uiState) {
        if (uiState is WaterUiState.Success || uiState is WaterUiState.Error) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearUiState()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("💧 Hidratación") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = {
            if (uiState is WaterUiState.Success) {
                Snackbar {
                    Text((uiState as WaterUiState.Success).message)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Progreso circular
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                CircularProgressIndicator(
                    progress = { dailyStats.progress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp,
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${dailyStats.totalAmount}ml",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "de ${dailyStats.goal}ml",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (dailyStats.isGoalReached) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "¡Meta alcanzada! 🎉",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Agregar agua",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botones de cantidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.addWater(100) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("100ml")
                }
                Button(
                    onClick = { viewModel.addWater(250) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("250ml")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.addWater(500) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("500ml")
                }
                Button(
                    onClick = { viewModel.addWater(1000) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("1L")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Historial del día
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Historial de hoy",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${dailyStats.intakes.size} registros",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Progreso: ${(dailyStats.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
