package com.example.project.data.model

enum class ReportType {
    SERVICIOS_PUBLICOS,
    ROBO_ASALTO,
    CORRUPCION,
    VIOLENCIA_GENERO,
    NARCOMENUDEO,
    REPORTE_GENERAL;
    
    fun getDisplayName(): String {
        return when(this) {
            SERVICIOS_PUBLICOS -> "Servicios Públicos"
            ROBO_ASALTO -> "Robo o Asalto"
            CORRUPCION -> "Corrupción u Omisión"
            VIOLENCIA_GENERO -> "Violencia de Género"
            NARCOMENUDEO -> "Narcomenudeo"
            REPORTE_GENERAL -> "Reporte General"
        }
    }
}
