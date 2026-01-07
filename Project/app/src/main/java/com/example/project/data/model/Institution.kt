package com.example.project.data.model

data class Institution(
    val id: String = "",
    val name: String = "",
    val category: String = "", // "seguridad", "agua", "género", "salud", "educación", "general"
    val address: String = "",
    val phone: String = "",
    val website: String = "",
    val schedule: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val description: String = ""
)
