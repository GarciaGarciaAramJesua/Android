package com.escom.gestordearchivos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.escom.gestordearchivos.domain.FileItem
import com.escom.gestordearchivos.domain.FileType
import com.escom.gestordearchivos.ui.components.*
import com.escom.gestordearchivos.utils.FileUtils
import com.escom.gestordearchivos.viewmodel.FileManagerViewModel
import kotlinx.coroutines.launch

/**
 * Pantalla principal del explorador de archivos
 */
@Composable
fun FileExplorerScreen(
    viewModel: FileManagerViewModel = viewModel(),
    onNavigateToViewer: (FileItem) -> Unit,
    onToggleTheme: () -> Unit,
    isLightMode: Boolean,
    onToggleLightMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Estados del ViewModel
    val fileList by viewModel.fileList.collectAsState()
    val currentDirectory by viewModel.currentDirectory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isGridView by viewModel.isGridView.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    // Estados locales de UI
    var isSearchActive by remember { mutableStateOf(false) }
    var selectedFile by remember { mutableStateOf<FileItem?>(null) }
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showOptionsDialog by remember { mutableStateOf(false) }
    
    // Estado de favorito del archivo seleccionado
    var isFavorite by remember { mutableStateOf(false) }

    // Actualizar estado de favorito cuando cambia el archivo seleccionado
    LaunchedEffect(selectedFile) {
        selectedFile?.let {
            isFavorite = viewModel.isFavorite(it.path)
        }
    }

    // Breadcrumbs
    val rootDirectory by remember { mutableStateOf(viewModel.getRootDirectory()) }
    val breadcrumbs = remember(currentDirectory) {
        FileUtils.getBreadcrumbs(
            rootDirectory,
            currentDirectory
        )
    }

    val pendingOp by viewModel.pendingOperation.collectAsState()

    Scaffold(
        topBar = {
            Column {
                FileManagerTopBar(
                    title = "Gestor de Archivos",
                    isSearchActive = isSearchActive,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.searchFiles(it) },
                    onSearchActiveChange = { 
                        isSearchActive = it
                        if (!it) viewModel.clearSearch()
                    },
                    onToggleViewMode = { viewModel.toggleViewMode() },
                    onToggleTheme = onToggleTheme,
                    isLightMode = isLightMode,
                    onToggleLightMode = onToggleLightMode,
                    onOpenSettings = { /* TODO: Implementar configuración */ },
                    isGridView = isGridView
                )
                if (!isSearchActive) {
                    Breadcrumbs(
                        breadcrumbs = breadcrumbs,
                        onNavigate = { viewModel.loadFiles(it) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (!isSearchActive) {
                Column {
                    if (pendingOp != null) {
                        ExtendedFloatingActionButton(
                            text = { Text("Pegar aquí") },
                            onClick = {
                                viewModel.pasteTo(
                                    destination = currentDirectory,
                                    onSuccess = {
                                        scope.launch { snackbarHostState.showSnackbar("Pegado correctamente") }
                                    },
                                    onError = { err -> scope.launch { snackbarHostState.showSnackbar(err) } }
                                )
                            },
                            icon = { Icon(Icons.Filled.ContentPaste, contentDescription = null) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    CreateFolderFab(
                        onClick = { showCreateFolderDialog = true }
                    )
                }
            }
        },
        snackbarHost = {
            // Usaremos SnackbarHostState para mostrar errores dinámicos (onError callbacks)
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.padding(16.dp))
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
                isSearchActive && searchQuery.isNotEmpty() -> {
                    FileListContent(
                        files = searchResults,
                        isGridView = isGridView,
                        onFileClick = { fileItem ->
                            if (fileItem.isDirectory) {
                                viewModel.navigateToDirectory(fileItem.file)
                                isSearchActive = false
                                viewModel.clearSearch()
                            } else {
                                viewModel.addToRecent(fileItem)
                                onNavigateToViewer(fileItem)
                            }
                        },
                        onFileLongClick = { fileItem ->
                            selectedFile = fileItem
                            showOptionsDialog = true
                        },
                        getFavoriteStatus = { path ->
                            scope.launch {
                                viewModel.isFavorite(path)
                            }
                            false
                        }
                    )
                }
                fileList.isEmpty() -> {
                    Text(
                        text = "Esta carpeta está vacía",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> {
                    FileListContent(
                        files = fileList,
                        isGridView = isGridView,
                        onFileClick = { fileItem ->
                            if (fileItem.isDirectory) {
                                viewModel.navigateToDirectory(fileItem.file)
                            } else {
                                viewModel.addToRecent(fileItem)
                                onNavigateToViewer(fileItem)
                            }
                        },
                        onFileLongClick = { fileItem ->
                            selectedFile = fileItem
                            showOptionsDialog = true
                        },
                        getFavoriteStatus = { path ->
                            scope.launch {
                                viewModel.isFavorite(path)
                            }
                            false
                        }
                    )
                }
            }
        }
    }

    // Diálogos
    if (showCreateFolderDialog) {
        CreateFolderDialog(
            onDismiss = { showCreateFolderDialog = false },
            onConfirm = { folderName ->
                viewModel.createFolder(
                    folderName = folderName,
                    onSuccess = { /* Éxito */ },
                    onError = { error ->
                        // Mostrar error en snackbar
                        scope.launch {
                            snackbarHostState.showSnackbar(error)
                        }
                    }
                )
            }
        )
    }

    selectedFile?.let { file ->
        if (showRenameDialog) {
            RenameFileDialog(
                currentName = file.name,
                onDismiss = { showRenameDialog = false },
                onConfirm = { newName ->
                    viewModel.renameFile(
                        file = file.file,
                        newName = newName,
                        onSuccess = { selectedFile = null },
                        onError = { _ -> /* Mostrar error */ }
                    )
                }
            )
        }

        if (showDeleteDialog) {
            DeleteConfirmationDialog(
                fileName = file.name,
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    viewModel.deleteFile(
                        file = file.file,
                        onSuccess = { selectedFile = null },
                        onError = { _ -> /* Mostrar error */ }
                    )
                }
            )
        }

        if (showOptionsDialog) {
            FileOptionsDialog(
                fileItem = file,
                isFavorite = isFavorite,
                onDismiss = { showOptionsDialog = false },
                onRename = { showRenameDialog = true },
                onCopy = { viewModel.startCopy(file.file); showOptionsDialog = false },
                onMove = { viewModel.startMove(file.file); showOptionsDialog = false },
                onDelete = { showDeleteDialog = true },
                onToggleFavorite = { 
                    viewModel.toggleFavorite(file)
                    isFavorite = !isFavorite
                },
                onOpenWith = {
                    FileUtils.openFileWithExternalApp(context, file.file)
                }
            )
        }
    }
}

/**
 * Contenido de la lista de archivos (lista o cuadrícula)
 */
@Composable
private fun FileListContent(
    files: List<FileItem>,
    isGridView: Boolean,
    onFileClick: (FileItem) -> Unit,
    onFileLongClick: (FileItem) -> Unit,
    getFavoriteStatus: (String) -> Boolean
) {
    if (isGridView) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(files) { fileItem ->
                FileGridItem(
                    fileItem = fileItem,
                    isFavorite = getFavoriteStatus(fileItem.path),
                    onClick = { onFileClick(fileItem) },
                    onLongClick = { onFileLongClick(fileItem) }
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(files) { fileItem ->
                FileListItem(
                    fileItem = fileItem,
                    isFavorite = getFavoriteStatus(fileItem.path),
                    onClick = { onFileClick(fileItem) },
                    onLongClick = { onFileLongClick(fileItem) }
                )
                HorizontalDivider()
            }
        }
    }
}
