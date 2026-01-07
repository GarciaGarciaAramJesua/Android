package com.example.project.data.repository

import com.example.project.data.model.Institution
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class InstitutionRepository {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val institutionsCollection = firestore.collection("institutions")
    
    suspend fun initializeInstitutions() {
        val count = institutionsCollection.get().await().size()
        if (count == 0) {
            // Inicializar con instituciones de ejemplo
            getDefaultInstitutions().forEach { institution ->
                institutionsCollection.document(institution.id).set(institution).await()
            }
        }
    }
    
    fun getAllInstitutions(): Flow<List<Institution>> = callbackFlow {
        val subscription = institutionsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val institutions = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Institution::class.java)
                } ?: emptyList()
                
                trySend(institutions)
            }
        
        awaitClose { subscription.remove() }
    }
    
    fun getInstitutionsByCategory(category: String): Flow<List<Institution>> = callbackFlow {
        val subscription = institutionsCollection
            .whereEqualTo("category", category)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val institutions = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Institution::class.java)
                } ?: emptyList()
                
                trySend(institutions)
            }
        
        awaitClose { subscription.remove() }
    }
    
    private fun getDefaultInstitutions(): List<Institution> {
        return listOf(
            Institution("1", "Secretaría de Seguridad Ciudadana CDMX", "seguridad", "Calle Liverpool 6, Col. Juárez, Cuauhtémoc", "5552080898", "https://www.ssc.cdmx.gob.mx", "24 horas", 19.426734, -99.162437, "Atención de emergencias y denuncias"),
            Institution("2", "Sistema de Aguas de la Ciudad de México", "agua", "Av. Río Churubusco y Eje 5 Sur, Iztapalapa", "5556541234", "https://www.sacmex.cdmx.gob.mx", "Lun-Vie 9:00-18:00", 19.368490, -99.100170, "Reportes de fugas y fallas en el servicio de agua"),
            Institution("3", "Instituto de las Mujeres CDMX", "género", "Tlaxcoaque 8, Centro Histórico", "5555120658", "https://www.inmujeres.cdmx.gob.mx", "Lun-Vie 9:00-18:00", 19.434020, -99.138790, "Atención a víctimas de violencia de género"),
            Institution("4", "Línea Mujeres", "género", "Atención telefónica", "5556581111", "https://www.inmujeres.cdmx.gob.mx", "24 horas", 19.432608, -99.133209, "Atención psicológica y jurídica a mujeres"),
            Institution("5", "Fiscalía General de Justicia CDMX", "seguridad", "Av. Insurgentes Sur 847, Nápoles", "5553464000", "https://www.fgjcdmx.gob.mx", "24 horas", 19.385210, -99.177850, "Denuncias y procuración de justicia"),
            Institution("6", "Comisión de Derechos Humanos CDMX", "general", "Av. Universidad 1449, Pueblo Axotla", "5556814822", "https://cdhcm.org.mx", "Lun-Vie 9:00-18:00", 19.346640, -99.183850, "Quejas contra servidores públicos"),
            Institution("7", "Locatel CDMX", "general", "Atención telefónica", "5556581111", "https://www.locatel.cdmx.gob.mx", "24 horas", 19.432608, -99.133209, "Orientación e información ciudadana"),
            Institution("8", "Centro de Comando y Control C5", "seguridad", "Atención telefónica", "911", "https://www.c5.cdmx.gob.mx", "24 horas", 19.403489, -99.155089, "Emergencias"),
            Institution("9", "Contraloría General CDMX", "general", "Tlaxcoaque 8, Centro Histórico", "5555129000", "https://www.contraloria.cdmx.gob.mx", "Lun-Vie 9:00-18:00", 19.434177, -99.138692, "Denuncias de corrupción"),
            Institution("10", "Protección Civil CDMX", "seguridad", "Av. Morelos 20, Valle Gómez", "5556834177", "https://www.proteccioncivil.cdmx.gob.mx", "24 horas", 19.451510, -99.120650, "Emergencias y prevención de riesgos"),
            
            Institution("11", "Instituto Nacional de las Mujeres", "género", "Alfonso Esparza Oteo 119, Guadalupe Inn", "5553220200", "https://www.gob.mx/inmujeres", "Lun-Vie 9:00-18:00", 19.363460, -99.183120, "Programas de apoyo a mujeres"),
            Institution("12", "Cruz Roja Mexicana", "salud", "Ejército Nacional 1032, Los Morales Polanco", "5553951111", "https://www.cruzrojamexicana.org.mx", "24 horas", 19.433040, -99.203110, "Atención médica de emergencia"),
            Institution("13", "Bomberos CDMX", "seguridad", "Atención telefónica", "5556831111", "https://www.heroico.cdmx.gob.mx", "24 horas", 19.432608, -99.133209, "Incendios y rescates"),
            Institution("14", "Comisión Nacional de Seguridad", "seguridad", "Av. Constituyentes 947, Lomas Altas", "5550819600", "https://www.gob.mx/sspc", "Lun-Vie 9:00-18:00", 19.402570, -99.247730, "Prevención del delito"),
            Institution("15", "DIF CDMX", "general", "Av. San Fernando 84, Toriello Guerra", "5555543887", "https://dif.cdmx.gob.mx", "Lun-Vie 9:00-18:00", 19.300210, -99.168520, "Asistencia social y familiar"),
            Institution("16", "CEJUM - Centros de Justicia para las Mujeres", "género", "Av. Río Churubusco 377, Granjas México", "5556324300", "https://www.fgjcdmx.gob.mx", "Lun-Vie 9:00-18:00", 19.386940, -99.118180, "Atención integral a mujeres víctimas"),
            Institution("17", "COPRED - Consejo para Prevenir la Discriminación", "general", "Av. Chapultepec 49, Centro", "5555129000", "https://copred.cdmx.gob.mx", "Lun-Vie 9:00-18:00", 19.433320, -99.145180, "Denuncias por discriminación"),
            Institution("18", "Comisión Ejecutiva de Atención a Víctimas CDMX", "general", "Dr. Lavista 144, Doctores", "5555120990", "https://www.atencionvictimas.cdmx.gob.mx", "Lun-Vie 9:00-18:00", 19.421980, -99.153780, "Apoyo integral a víctimas"),
            Institution("19", "Instituto Nacional Electoral CDMX", "general", "Viaducto Tlalpan 100, Arenal Tepepan", "5554817000", "https://www.ine.mx", "Lun-Vie 9:00-18:00", 19.290660, -99.175590, "Asuntos electorales"),
            Institution("20", "Secretaría del Medio Ambiente CDMX", "general", "Av. Ejército Nacional 223, Anáhuac", "5555780931", "https://www.sedema.cdmx.gob.mx", "Lun-Vie 9:00-18:00", 19.442370, -99.190510, "Denuncias ambientales"),
            
            Institution("21", "Agencia Digital de Innovación Pública", "general", "Versalles 49, Juárez", "5555122627", "https://adip.cdmx.gob.mx", "Lun-Vie 9:00-18:00", 19.424530, -99.168060, "Gobierno digital"),
            Institution("22", "Instituto de Verificación Administrativa", "general", "Av. Insurgentes Norte 1359, Guadalupe Insurgentes", "5555912000", "https://www.invea.cdmx.gob.mx", "Lun-Vie 9:00-18:00", 19.484680, -99.137330, "Denuncias de irregularidades"),
            Institution("23", "Procuraduría Ambiental y del Ordenamiento Territorial", "general", "Montes Urales 758, Lomas de Chapultepec", "5555540780", "https://www.paot.org.mx", "Lun-Vie 9:00-18:00", 19.427750, -99.215320, "Denuncias ambientales"),
            Institution("24", "Instituto de la Defensoría Pública", "general", "Dr. Andrade 351, San Simón Ticumac", "5555929800", "https://www.defensoria.cdmx.gob.mx", "Lun-Vie 9:00-18:00", 19.404830, -99.148910, "Defensoría de oficio"),
            Institution("25", "Secretaría de Movilidad CDMX", "general", "Álvaro Obregón 269, Roma Norte", "5555209520", "https://www.semovi.cdmx.gob.mx", "Lun-Vie 9:00-18:00", 19.419270, -99.161580, "Transporte y vialidad"),
            Institution("26", "Red de Transporte de Pasajeros", "general", "Delicias 67, Centro", "5555187190", "https://www.rtp.cdmx.gob.mx", "Lun-Dom 5:00-24:00", 19.432890, -99.150120, "Transporte público"),
            Institution("27", "Autoridad del Centro Histórico", "general", "República de Guatemala 34, Centro Histórico", "5555229484", "https://www.ach.cdmx.gob.mx", "Lun-Vie 9:00-18:00", 19.434750, -99.132670, "Normatividad del Centro Histórico"),
            Institution("28", "Secretaría de Educación CDMX", "educación", "Dr. Río de la Loza 151, Doctores", "3601-1000", "https://www.educacion.cdmx.gob.mx", "Lun-Vie 9:00-18:00", 19.419870, -99.149560, "Denuncias educativas"),
            Institution("29", "Secretaría de Salud CDMX", "salud", "Xocongo 225, Tránsito", "5556583800", "https://www.salud.cdmx.gob.mx", "Lun-Vie 9:00-18:00", 19.433580, -99.127940, "Servicios de salud"),
            Institution("30", "Instituto del Deporte CDMX", "general", "Añil 670, Granjas México", "5556484950", "https://www.indeporte.cdmx.gob.mx", "Lun-Vie 9:00-18:00", 19.390760, -99.122640, "Programas deportivos")
        )
    }
}
