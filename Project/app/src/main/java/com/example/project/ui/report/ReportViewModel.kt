package com.example.project.ui.report

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.data.model.Report
import com.example.project.data.repository.ReportRepository
import kotlinx.coroutines.launch

class ReportViewModel : ViewModel() {
    
    private val repository = ReportRepository()
    
    private val _reportCreationStatus = MutableLiveData<ReportCreationStatus>()
    val reportCreationStatus: LiveData<ReportCreationStatus> = _reportCreationStatus
    
    private val _reports = MutableLiveData<List<Report>>()
    val reports: LiveData<List<Report>> = _reports
    
    fun createReport(report: Report, photoUri: Uri?) {
        viewModelScope.launch {
            _reportCreationStatus.value = ReportCreationStatus.Loading
            
            val result = repository.createReport(report, photoUri)
            
            _reportCreationStatus.value = if (result.isSuccess) {
                ReportCreationStatus.Success(result.getOrNull() ?: "")
            } else {
                ReportCreationStatus.Error(result.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }
    
    fun loadAllReports() {
        viewModelScope.launch {
            repository.getAllReports().collect { reportsList ->
                _reports.value = reportsList
            }
        }
    }
    
    fun loadReportsByType(type: String) {
        viewModelScope.launch {
            repository.getReportsByType(type).collect { reportsList ->
                _reports.value = reportsList
            }
        }
    }
}

sealed class ReportCreationStatus {
    object Loading : ReportCreationStatus()
    data class Success(val reportId: String) : ReportCreationStatus()
    data class Error(val message: String) : ReportCreationStatus()
}
