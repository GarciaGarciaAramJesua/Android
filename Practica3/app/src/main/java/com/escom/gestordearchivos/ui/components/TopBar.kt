package com.escom.gestordearchivos.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.io.File

/**
 * Barra superior de la aplicación con breadcrumbs y acciones
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileManagerTopBar(
    title: String,
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    onToggleViewMode: () -> Unit,
    onToggleTheme: () -> Unit,
    // Nuevo: controla modo claro/oscuro independiente del tema seleccionado
    isLightMode: Boolean = true,
    onToggleLightMode: () -> Unit,
    onOpenSettings: () -> Unit,
    isGridView: Boolean,
    modifier: Modifier = Modifier
) {
    if (isSearchActive) {
        SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            onDismiss = { onSearchActiveChange(false) },
            modifier = modifier
        )
    } else {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            actions = {
                IconButton(onClick = { onSearchActiveChange(true) }) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                }
                IconButton(onClick = onToggleViewMode) {
                    Icon(
                        if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                        contentDescription = "Cambiar vista"
                    )
                }
                // Botón para alternar entre colores (Guinda/Azul)
                IconButton(onClick = onToggleTheme) {
                    Icon(Icons.Default.Palette, contentDescription = "Cambiar tema")
                }
                // Botón para alternar modo claro/oscuro manteniendo la paleta actual
                IconButton(onClick = onToggleLightMode) {
                    Icon(
                        imageVector = if (isLightMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = "Alternar modo claro/oscuro"
                    )
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = modifier
        )
    }
}

/**
 * Barra de búsqueda expandida
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Buscar archivos...") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Default.ArrowBack, 
                    contentDescription = "Cerrar búsqueda",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        actions = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Default.Clear, 
                        contentDescription = "Limpiar",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    )
}

/**
 * Breadcrumbs - Muestra la ruta actual de navegación
 */
@Composable
fun Breadcrumbs(
    breadcrumbs: List<Pair<String, File>>,
    onNavigate: (File) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            breadcrumbs.forEachIndexed { index, (name, file) ->
                if (index > 0) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(horizontal = 4.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(
                    onClick = { onNavigate(file) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Botón de acción flotante para crear nueva carpeta
 */
@Composable
fun CreateFolderFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(
            Icons.Default.CreateNewFolder,
            contentDescription = "Nueva carpeta",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}
