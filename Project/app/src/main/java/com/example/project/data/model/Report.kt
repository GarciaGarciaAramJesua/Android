package com.example.project.data.model

import com.google.firebase.firestore.GeoPoint
import java.util.Date

data class Report(
    val id: String = "",
    val type: String = "",
    val alias: String = "",
    val description: String = "",
    val location: GeoPoint? = null,
    val locationAddress: String = "",
    val photoUrl: String = "",
    val timestamp: Date = Date(),
    
    // Servicios Públicos
    val serviceType: String = "", // "baches", "luminarias", "fugas"
    
    // Robo o Asalto
    val stolenObjects: String = "",
    
    // Corrupción
    val dependencyName: String = "",
    val officialName: String = "",
    
    // Violencia de Género
    val violenceType: String = "", // "física", "psicológica", "sexual", "económica"
    val aggressorRelation: String = "",
    
    // Narcomenudeo
    val suspiciousActivity: String = "",
    val personDescription: String = "",
    val vehicleDescription: String = "",
    
    // Metadata
    val status: String = "pending" // pending, in_progress, resolved
) {
    constructor() : this(
        id = "",
        type = "",
        alias = "",
        description = "",
        location = null,
        locationAddress = "",
        photoUrl = "",
        timestamp = Date()
    )
}
