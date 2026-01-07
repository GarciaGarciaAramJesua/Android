package com.example.project.data.repository

import android.net.Uri
import com.example.project.data.model.Report
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ReportRepository {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val reportsCollection = firestore.collection("reports")
    
    suspend fun createReport(report: Report, photoUri: Uri?): Result<String> {
        return try {
            val reportId = UUID.randomUUID().toString()
            val photoUrl = photoUri?.let { uploadPhoto(it, reportId) } ?: ""
            
            val reportWithId = report.copy(
                id = reportId,
                photoUrl = photoUrl
            )
            
            reportsCollection.document(reportId).set(reportWithId).await()
            Result.success(reportId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun uploadPhoto(uri: Uri, reportId: String): String {
        val fileName = "reports/$reportId/${UUID.randomUUID()}.jpg"
        val storageRef = storage.reference.child(fileName)
        
        storageRef.putFile(uri).await()
        return storageRef.downloadUrl.await().toString()
    }
    
    fun getAllReports(): Flow<List<Report>> = callbackFlow {
        val subscription = reportsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val reports = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Report::class.java)
                } ?: emptyList()
                
                trySend(reports)
            }
        
        awaitClose { subscription.remove() }
    }
    
    fun getReportsByType(type: String): Flow<List<Report>> = callbackFlow {
        val subscription = reportsCollection
            .whereEqualTo("type", type)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val reports = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Report::class.java)
                } ?: emptyList()
                
                trySend(reports)
            }
        
        awaitClose { subscription.remove() }
    }
    
    suspend fun getReportById(reportId: String): Result<Report> {
        return try {
            val document = reportsCollection.document(reportId).get().await()
            val report = document.toObject(Report::class.java)
            if (report != null) {
                Result.success(report)
            } else {
                Result.failure(Exception("Reporte no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateReportStatus(reportId: String, status: String): Result<Unit> {
        return try {
            reportsCollection.document(reportId)
                .update("status", status)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
