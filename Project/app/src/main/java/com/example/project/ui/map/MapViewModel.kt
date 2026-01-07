package com.example.project.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.data.model.Report
import com.example.project.data.model.SeverityLevel
import com.example.project.data.model.ZoneData
import com.example.project.data.repository.ReportRepository
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    
    private val repository = ReportRepository()
    
    private val _reports = MutableLiveData<List<Report>>()
    val reports: LiveData<List<Report>> = _reports
    
    private val _selectedReport = MutableLiveData<Report?>()
    val selectedReport: LiveData<Report?> = _selectedReport
    
    private val _zoneData = MutableLiveData<List<ZoneData>>()
    val zoneData: LiveData<List<ZoneData>> = _zoneData
    
    init {
        loadReports()
    }
    
    private fun loadReports() {
        viewModelScope.launch {
            repository.getAllReports().collect { reportsList ->
                _reports.value = reportsList
                calculateZoneData(reportsList)
            }
        }
    }
    
    fun selectReport(report: Report) {
        _selectedReport.value = report
    }
    
    fun clearSelectedReport() {
        _selectedReport.value = null
    }
    
    private fun calculateZoneData(reports: List<Report>) {
        // Agrupar reportes por zonas (alcaldías)
        val zoneMap = mutableMapOf<String, Int>()
        
        reports.forEach { report ->
            val zone = report.locationAddress.split(",").firstOrNull()?.trim() ?: "Desconocida"
            zoneMap[zone] = (zoneMap[zone] ?: 0) + 1
        }
        
        val zoneDataList = zoneMap.map { (zone, count) ->
            ZoneData(
                zoneName = zone,
                reportsCount = count,
                severity = SeverityLevel.fromCount(count)
            )
        }
        
        _zoneData.value = zoneDataList
    }
}
