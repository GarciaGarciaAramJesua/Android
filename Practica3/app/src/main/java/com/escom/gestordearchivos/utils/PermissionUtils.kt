package com.escom.gestordearchivos.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.ContextCompat

/**
 * Utilidad para gestionar permisos de almacenamiento según la versión de Android
 * Implementa Scoped Storage para Android 10+ y permisos tradicionales para versiones anteriores
 */
object PermissionUtils {

    /**
     * Lista de permisos necesarios según la versión de Android
     */
    fun getRequiredPermissions(): List<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Android 13+ (API 33+) - Permisos granulares por tipo de medio
                listOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                // Android 11+ (API 30+) - Requiere MANAGE_EXTERNAL_STORAGE
                emptyList() // Manejado por checkStoragePermission
            }
            else -> {
                // Android 10 y anteriores - Permisos tradicionales
                listOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }

    /**
     * Verifica si se tienen todos los permisos necesarios
     */
    fun hasStoragePermissions(context: Context): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Android 13+ - Verificar permisos granulares
                getRequiredPermissions().all { permission ->
                    ContextCompat.checkSelfPermission(context, permission) ==
                            PackageManager.PERMISSION_GRANTED
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                // Android 11-12: verificar MANAGE_EXTERNAL_STORAGE
                Environment.isExternalStorageManager()
            }
            else -> {
                // Android 10 y anteriores - Verificar permisos tradicionales
                getRequiredPermissions().all { permission ->
                    ContextCompat.checkSelfPermission(context, permission) ==
                            PackageManager.PERMISSION_GRANTED
                }
            }
        }
    }

    /**
     * Crea un intent para solicitar el permiso MANAGE_EXTERNAL_STORAGE (Android 11+)
     */
    fun createManageStorageIntent(context: Context): Intent {
        return Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
            data = Uri.parse("package:${context.packageName}")
        }
    }

    /**
     * Verifica si el permiso MANAGE_EXTERNAL_STORAGE está disponible
     */
    fun canRequestManageStorage(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

    /**
     * Obtiene el mensaje de justificación para los permisos según la versión
     */
    fun getPermissionRationale(): String {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                "Esta aplicación necesita acceso completo al almacenamiento para poder " +
                "explorar, visualizar y gestionar todos tus archivos y carpetas. " +
                "Por favor, habilita el acceso en la configuración."
            }
            else -> {
                "Esta aplicación necesita permisos de lectura y escritura en el almacenamiento " +
                "para explorar, visualizar y gestionar tus archivos."
            }
        }
    }

    /**
     * Obtiene la descripción de los permisos solicitados para documentación
     */
    fun getPermissionDescriptions(): Map<String, String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                mapOf(
                    "READ_MEDIA_IMAGES" to "Permite visualizar archivos de imagen en tu dispositivo",
                    "READ_MEDIA_VIDEO" to "Permite visualizar archivos de video en tu dispositivo",
                    "READ_MEDIA_AUDIO" to "Permite visualizar archivos de audio en tu dispositivo"
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                mapOf(
                    "MANAGE_EXTERNAL_STORAGE" to "Permite acceso completo a todos los archivos " +
                        "del almacenamiento para navegación, lectura, escritura y gestión"
                )
            }
            else -> {
                mapOf(
                    "READ_EXTERNAL_STORAGE" to "Permite leer archivos del almacenamiento externo",
                    "WRITE_EXTERNAL_STORAGE" to "Permite crear, modificar y eliminar archivos"
                )
            }
        }
    }
}
