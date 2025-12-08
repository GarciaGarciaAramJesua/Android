package com.example.wearable.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wearable.data.model.*
import com.example.wearable.data.repository.HealthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar la hidratación
 */
class WaterViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = HealthRepository(application)
    
    val dailyStats: StateFlow<DailyWaterStats> = repository.getDailyWaterStats()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DailyWaterStats(
                date = java.time.LocalDate.now(),
                totalAmount = 0,
                goal = 2000
            )
        )
    
    val reminderSettings: StateFlow<WaterReminderSettings> = repository.getWaterReminderSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WaterReminderSettings()
        )
    
    private val _uiState = MutableStateFlow<WaterUiState>(WaterUiState.Idle)
    val uiState: StateFlow<WaterUiState> = _uiState.asStateFlow()
    
    fun addWater(amount: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = WaterUiState.Loading
                repository.addWaterIntake(amount)
                _uiState.value = WaterUiState.Success("¡Agua registrada! +${amount}ml")
            } catch (e: Exception) {
                _uiState.value = WaterUiState.Error(e.message ?: "Error al registrar agua")
            }
        }
    }
    
    fun updateGoal(newGoal: Int) {
        viewModelScope.launch {
            try {
                val currentSettings = reminderSettings.value
                repository.saveWaterReminderSettings(
                    currentSettings.copy(dailyGoal = newGoal)
                )
                _uiState.value = WaterUiState.Success("Meta actualizada a ${newGoal}ml")
            } catch (e: Exception) {
                _uiState.value = WaterUiState.Error(e.message ?: "Error al actualizar meta")
            }
        }
    }
    
    fun updateReminderSettings(settings: WaterReminderSettings) {
        viewModelScope.launch {
            try {
                repository.saveWaterReminderSettings(settings)
                _uiState.value = WaterUiState.Success("Configuración guardada")
            } catch (e: Exception) {
                _uiState.value = WaterUiState.Error(e.message ?: "Error al guardar configuración")
            }
        }
    }
    
    fun clearUiState() {
        _uiState.value = WaterUiState.Idle
    }
}

sealed class WaterUiState {
    object Idle : WaterUiState()
    object Loading : WaterUiState()
    data class Success(val message: String) : WaterUiState()
    data class Error(val message: String) : WaterUiState()
}
