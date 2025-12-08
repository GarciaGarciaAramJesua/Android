package com.example.wearable.mobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wearable.mobile.data.model.*
import com.example.wearable.mobile.data.repository.HealthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ActiveBreakViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = HealthRepository(application)
    
    val todayBreaks: StateFlow<List<ActiveBreakRecord>> = repository.getTodayActiveBreaks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val reminderSettings: StateFlow<ActiveBreakReminderSettings> = 
        repository.getActiveBreakReminderSettings()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ActiveBreakReminderSettings()
            )
    
    private val _availableBreaks = MutableStateFlow(PredefinedBreaks.getAll())
    val availableBreaks: StateFlow<List<ActiveBreak>> = _availableBreaks.asStateFlow()
    
    private val _currentBreak = MutableStateFlow<ActiveBreak?>(null)
    val currentBreak: StateFlow<ActiveBreak?> = _currentBreak.asStateFlow()
    
    private val _currentExerciseIndex = MutableStateFlow(0)
    val currentExerciseIndex: StateFlow<Int> = _currentExerciseIndex.asStateFlow()
    
    private val _uiState = MutableStateFlow<ActiveBreakUiState>(ActiveBreakUiState.Idle)
    val uiState: StateFlow<ActiveBreakUiState> = _uiState.asStateFlow()
    
    fun startBreak(activeBreak: ActiveBreak) {
        _currentBreak.value = activeBreak
        _currentExerciseIndex.value = 0
        _uiState.value = ActiveBreakUiState.InProgress
    }
    
    fun nextExercise() {
        val current = _currentBreak.value ?: return
        val nextIndex = _currentExerciseIndex.value + 1
        
        if (nextIndex < current.exercises.size) {
            _currentExerciseIndex.value = nextIndex
        } else {
            completeBreak()
        }
    }
    
    fun previousExercise() {
        val prevIndex = _currentExerciseIndex.value - 1
        if (prevIndex >= 0) {
            _currentExerciseIndex.value = prevIndex
        }
    }
    
    fun completeBreak() {
        val break_ = _currentBreak.value ?: return
        viewModelScope.launch {
            try {
                repository.recordActiveBreak(break_, completed = true)
                _uiState.value = ActiveBreakUiState.Completed("¡Pausa activa completada!")
                _currentBreak.value = null
                _currentExerciseIndex.value = 0
            } catch (e: Exception) {
                _uiState.value = ActiveBreakUiState.Error(e.message ?: "Error al completar pausa")
            }
        }
    }
    
    fun skipBreak() {
        val break_ = _currentBreak.value ?: return
        viewModelScope.launch {
            try {
                repository.recordActiveBreak(break_, completed = false)
                _uiState.value = ActiveBreakUiState.Idle
                _currentBreak.value = null
                _currentExerciseIndex.value = 0
            } catch (e: Exception) {
                _uiState.value = ActiveBreakUiState.Error(e.message ?: "Error")
            }
        }
    }
    
    fun updateReminderSettings(settings: ActiveBreakReminderSettings) {
        viewModelScope.launch {
            try {
                repository.saveActiveBreakReminderSettings(settings)
                _uiState.value = ActiveBreakUiState.Success("Configuración guardada")
            } catch (e: Exception) {
                _uiState.value = ActiveBreakUiState.Error(e.message ?: "Error al guardar")
            }
        }
    }
    
    fun clearUiState() {
        _uiState.value = ActiveBreakUiState.Idle
    }
}

sealed class ActiveBreakUiState {
    object Idle : ActiveBreakUiState()
    object InProgress : ActiveBreakUiState()
    data class Success(val message: String) : ActiveBreakUiState()
    data class Completed(val message: String) : ActiveBreakUiState()
    data class Error(val message: String) : ActiveBreakUiState()
}
