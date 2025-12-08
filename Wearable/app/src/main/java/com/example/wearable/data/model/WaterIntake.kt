package com.example.wearable.data.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Representa el registro de consumo de agua
 */
data class WaterIntake(
    val id: String = java.util.UUID.randomUUID().toString(),
    val amount: Int, // en ml
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val date: LocalDate = LocalDate.now()
)

/**
 * Estadísticas diarias de hidratación
 */
data class DailyWaterStats(
    val date: LocalDate,
    val totalAmount: Int, // en ml
    val goal: Int = 2000, // meta diaria en ml
    val intakes: List<WaterIntake> = emptyList()
) {
    val progress: Float
        get() = (totalAmount.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
    
    val isGoalReached: Boolean
        get() = totalAmount >= goal
}
