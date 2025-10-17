package com.escom.gestordearchivos

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import android.Manifest
import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.escom.gestordearchivos.domain.FileItem
import com.escom.gestordearchivos.ui.screens.FileExplorerScreen
import com.escom.gestordearchivos.ui.screens.FileViewerScreen
import com.escom.gestordearchivos.ui.theme.AppTheme
import com.escom.gestordearchivos.ui.theme.FileManagerTheme
import com.escom.gestordearchivos.data.ThemePreferences
import com.escom.gestordearchivos.utils.PermissionUtils
import com.escom.gestordearchivos.viewmodel.FileManagerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // Launcher para solicitar MANAGE_EXTERNAL_STORAGE en Android 11+
    private val manageStorageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // Verificar si se otorgaron los permisos después de volver
        recreate()
    }

    // Launcher para solicitar permisos granulares en Android 13+
    private val requestMediaPermissionsLauncher = registerForActivityResult(
        RequestMultiplePermissions()
    ) { permissions ->
        // Si al menos uno de los permisos fue concedido, recrear/actualizar UI
        val granted = permissions.values.any { it }
        if (granted) {
            recreate()
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Leer preferencias desde DataStore (si existen)
            val savedIsLight by ThemePreferences.isLightModeFlow(this@MainActivity).collectAsState(initial = true)
            val savedTheme by ThemePreferences.selectedThemeFlow(this@MainActivity).collectAsState(initial = AppTheme.GUINDA_IPN)

            var currentTheme by remember { mutableStateOf(savedTheme) }
            // Evaluar primero si el sistema está en modo oscuro (es un composable)
            val isSystemDark = isSystemInDarkTheme()
            // Estado que controla si la app usa modo claro (true) o modo oscuro (false)
            var isLightMode by remember { mutableStateOf(savedIsLight && !isSystemDark) }

            FileManagerTheme(
                selectedTheme = currentTheme,
                // FileManagerTheme espera `darkTheme`, así que invertimos el booleano
                darkTheme = !isLightMode
            ) {
                val hasStoragePermission = PermissionUtils.hasStoragePermissions(this)

                if (hasStoragePermission) {
                    val coroutineScope = rememberCoroutineScope()

                    FileManagerApp(
                        onToggleTheme = {
                            currentTheme = when (currentTheme) {
                                AppTheme.GUINDA_IPN -> AppTheme.AZUL_ESCOM
                                AppTheme.AZUL_ESCOM -> AppTheme.GUINDA_IPN
                            }
                            // Guardar la preferencia
                            coroutineScope.launch {
                                ThemePreferences.saveSelectedTheme(this@MainActivity, currentTheme)
                            }
                        },
                        isLightMode = isLightMode,
                        onToggleLightMode = {
                            isLightMode = !isLightMode
                            coroutineScope.launch {
                                ThemePreferences.saveIsLightMode(this@MainActivity, isLightMode)
                            }
                        }
                    )
                } else {
                    PermissionRequestScreen(
                        onRequestPermissions = {
                            // En Android 13+ pedimos permisos granulares; en Android 11-12
                            // redirigimos a la pantalla de configuración para MANAGE_EXTERNAL_STORAGE
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                requestMediaPermissionsLauncher.launch(arrayOf(
                                    Manifest.permission.READ_MEDIA_IMAGES,
                                    Manifest.permission.READ_MEDIA_VIDEO,
                                    Manifest.permission.READ_MEDIA_AUDIO
                                ))
                            } else if (PermissionUtils.canRequestManageStorage()) {
                                requestManageStoragePermission()
                            }
                        }
                    )
                }
            }
        }
    }

    /**
     * Solicita el permiso MANAGE_EXTERNAL_STORAGE para Android 11+
     */
    private fun requestManageStoragePermission() {
        val intent = PermissionUtils.createManageStorageIntent(this)
        manageStorageLauncher.launch(intent)
    }
}

/**
 * Aplicación principal con navegación
 */
@Composable
fun FileManagerApp(
    onToggleTheme: () -> Unit,
    isLightMode: Boolean,
    onToggleLightMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    var selectedFile by remember { mutableStateOf<FileItem?>(null) }

    NavHost(
        navController = navController,
        startDestination = "explorer",
        modifier = modifier
    ) {
        composable("explorer") {
            FileExplorerScreen(
                onNavigateToViewer = { fileItem ->
                    selectedFile = fileItem
                    navController.navigate("viewer")
                },
                onToggleTheme = onToggleTheme,
                isLightMode = isLightMode,
                onToggleLightMode = onToggleLightMode
            )
        }

        composable("viewer") {
            selectedFile?.let { file ->
                FileViewerScreen(
                    fileItem = file,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

/**
 * Pantalla para solicitar permisos
 */
@Composable
fun PermissionRequestScreen(
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Permisos necesarios",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = PermissionUtils.getPermissionRationale(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Mostrar permisos requeridos
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Permisos requeridos:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    PermissionUtils.getPermissionDescriptions().forEach { (_, description) ->
                        Text(
                            text = "• $description",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onRequestPermissions,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Otorgar permisos",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
