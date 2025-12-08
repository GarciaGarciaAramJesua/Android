package com.example.wearable.data.model

import java.time.LocalDateTime

/**
 * Representa un tipo de pausa activa con ejercicios
 */
data class ActiveBreak(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val exercises: List<Exercise>,
    val durationMinutes: Int,
    val icon: String = "🏃"
)

/**
 * Ejercicio individual dentro de una pausa activa
 */
data class Exercise(
    val name: String,
    val description: String,
    val durationSeconds: Int,
    val repetitions: Int = 1,
    val imageResource: String? = null
)

/**
 * Registro de pausa activa completada
 */
data class ActiveBreakRecord(
    val id: String = java.util.UUID.randomUUID().toString(),
    val breakType: ActiveBreak,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val completed: Boolean = true,
    val exercisesCompleted: Int = 0
)

/**
 * Ejercicios predefinidos para pausas activas
 */
object PredefinedBreaks {
    val quickStretch = ActiveBreak(
        name = "Estiramiento Rápido",
        exercises = listOf(
            Exercise("Estiramiento de cuello", "Gira suavemente la cabeza de lado a lado", 30, 2),
            Exercise("Estiramiento de brazos", "Estira los brazos hacia arriba", 30, 2),
            Exercise("Rotación de hombros", "Rota los hombros hacia atrás", 30, 2)
        ),
        durationMinutes = 3,
        icon = "🤸"
    )
    
    val eyeRest = ActiveBreak(
        name = "Descanso Visual",
        exercises = listOf(
            Exercise("Regla 20-20-20", "Mira a 20 pies de distancia por 20 segundos", 20, 1),
            Exercise("Parpadeo", "Parpadea rápidamente", 10, 3),
            Exercise("Masaje ocular", "Cierra los ojos y masajea suavemente", 30, 1)
        ),
        durationMinutes = 2,
        icon = "👁️"
    )
    
    val breathing = ActiveBreak(
        name = "Respiración Profunda",
        exercises = listOf(
            Exercise("Respiración 4-7-8", "Inhala 4 seg, sostén 7, exhala 8", 19, 3),
            Exercise("Respiración abdominal", "Respira profundamente con el abdomen", 30, 2)
        ),
        durationMinutes = 2,
        icon = "🫁"
    )
    
    val deskExercises = ActiveBreak(
        name = "Ejercicios de Escritorio",
        exercises = listOf(
            Exercise("Sentadillas", "Levántate y siéntate sin usar las manos", 30, 10),
            Exercise("Extensión de piernas", "Extiende las piernas mientras estás sentado", 30, 10),
            Exercise("Flexiones de pared", "Apóyate en la pared y haz flexiones", 30, 10)
        ),
        durationMinutes = 5,
        icon = "💪"
    )
    
    fun getAll() = listOf(quickStretch, eyeRest, breathing, deskExercises)
}
