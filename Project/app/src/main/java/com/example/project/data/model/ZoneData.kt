package com.example.project.data.model

data class ZoneData(
    val zoneName: String,
    val reportsCount: Int,
    val severity: SeverityLevel
)

enum class SeverityLevel {
    LOW,    // Verde
    MEDIUM, // Amarillo
    HIGH;   // Rojo
    
    fun getColor(): Int {
        return when(this) {
            LOW -> android.graphics.Color.GREEN
            MEDIUM -> android.graphics.Color.YELLOW
            HIGH -> android.graphics.Color.RED
        }
    }
    
    companion object {
        fun fromCount(count: Int): SeverityLevel {
            return when {
                count < 5 -> LOW
                count < 15 -> MEDIUM
                else -> HIGH
            }
        }
    }
}
