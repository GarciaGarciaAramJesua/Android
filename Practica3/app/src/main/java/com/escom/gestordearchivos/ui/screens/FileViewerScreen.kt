package com.escom.gestordearchivos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.escom.gestordearchivos.domain.FileItem
import com.escom.gestordearchivos.domain.FileType
import com.escom.gestordearchivos.utils.FileUtils
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.zoomable

/**
 * Pantalla para visualizar archivos de texto e imágenes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileViewerScreen(
    fileItem: FileItem,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fileContent by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Cargar contenido del archivo
    LaunchedEffect(fileItem) {
        when (fileItem.getFileType()) {
            FileType.TEXT -> {
                if (FileUtils.isTextFileTooLarge(fileItem.file)) {
                    errorMessage = "El archivo es demasiado grande para visualizar (> 5 MB)"
                    isLoading = false
                } else {
                    FileUtils.readTextFile(fileItem.file).fold(
                        onSuccess = { 
                            fileContent = it
                            isLoading = false
                        },
                        onFailure = { 
                            errorMessage = "Error al leer el archivo: ${it.message}"
                            isLoading = false
                        }
                    )
                }
            }
            FileType.IMAGE -> {
                isLoading = false
            }
            else -> {
                errorMessage = "Tipo de archivo no soportado para visualización"
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(fileItem.name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Error desconocido",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                fileItem.getFileType() == FileType.TEXT && fileContent != null -> {
                    TextFileViewer(content = fileContent!!)
                }
                fileItem.getFileType() == FileType.IMAGE -> {
                    ImageFileViewer(fileItem = fileItem)
                }
            }
        }
    }
}

/**
 * Visor de archivos de texto
 */
@Composable
private fun TextFileViewer(
    content: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        SelectionContainer {
            Text(
                text = content,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }
    }
}

/**
 * Visor de imágenes con zoom y desplazamiento
 */
@Composable
private fun ImageFileViewer(
    fileItem: FileItem,
    modifier: Modifier = Modifier
) {
    val zoomableState = rememberZoomableState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .zoomable(zoomableState),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = fileItem.file,
            contentDescription = fileItem.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}
