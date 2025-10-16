package com.escom.gestordearchivos.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.escom.gestordearchivos.domain.FileItem

/**
 * Diálogo para crear una nueva carpeta
 */
@Composable
fun CreateFolderDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var folderName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.CreateNewFolder, contentDescription = null) },
        title = { Text("Nueva carpeta") },
        text = {
            Column {
                OutlinedTextField(
                    value = folderName,
                    onValueChange = {
                        folderName = it
                        errorMessage = null
                    },
                    label = { Text("Nombre de la carpeta") },
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        folderName.isBlank() -> errorMessage = "El nombre no puede estar vacío"
                        !isValidFileName(folderName) -> errorMessage = "Nombre inválido"
                        else -> {
                            onConfirm(folderName)
                            onDismiss()
                        }
                    }
                }
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Diálogo para renombrar archivo
 */
@Composable
fun RenameFileDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newName by remember { mutableStateOf(currentName) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.DriveFileRenameOutline, contentDescription = null) },
        title = { Text("Renombrar") },
        text = {
            Column {
                OutlinedTextField(
                    value = newName,
                    onValueChange = {
                        newName = it
                        errorMessage = null
                    },
                    label = { Text("Nuevo nombre") },
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        newName.isBlank() -> errorMessage = "El nombre no puede estar vacío"
                        !isValidFileName(newName) -> errorMessage = "Nombre inválido"
                        newName == currentName -> onDismiss()
                        else -> {
                            onConfirm(newName)
                            onDismiss()
                        }
                    }
                }
            ) {
                Text("Renombrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Diálogo de confirmación para eliminar
 */
@Composable
fun DeleteConfirmationDialog(
    fileName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { 
            Icon(
                Icons.Default.DeleteForever, 
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            ) 
        },
        title = { Text("Confirmar eliminación") },
        text = { 
            Text("¿Estás seguro de que deseas eliminar '$fileName'? Esta acción no se puede deshacer.") 
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Diálogo de opciones del archivo (menú contextual)
 */
@Composable
fun FileOptionsDialog(
    fileItem: FileItem,
    isFavorite: Boolean,
    onDismiss: () -> Unit,
    onRename: () -> Unit,
    onCopy: () -> Unit,
    onMove: () -> Unit,
    onDelete: () -> Unit,
    onToggleFavorite: () -> Unit,
    onOpenWith: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
        title = { Text(fileItem.name, maxLines = 1) },
        text = {
            Column {
                FileOptionItem(
                    icon = Icons.Default.DriveFileRenameOutline,
                    text = "Renombrar",
                    onClick = { onRename(); onDismiss() }
                )
                FileOptionItem(
                    icon = Icons.Default.ContentCopy,
                    text = "Copiar",
                    onClick = { onCopy(); onDismiss() }
                )
                FileOptionItem(
                    icon = Icons.Filled.DriveFileMove,
                    text = "Mover",
                    onClick = { onMove(); onDismiss() }
                )
                FileOptionItem(
                    icon = if (isFavorite) Icons.Default.StarBorder else Icons.Default.Star,
                    text = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                    onClick = { onToggleFavorite(); onDismiss() }
                )
                if (!fileItem.isDirectory) {
                    FileOptionItem(
                        icon = Icons.Filled.OpenInNew,
                        text = "Abrir con...",
                        onClick = { onOpenWith(); onDismiss() }
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                FileOptionItem(
                    icon = Icons.Default.Delete,
                    text = "Eliminar",
                    onClick = { onDelete(); onDismiss() },
                    isDestructive = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun FileOptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (isDestructive) 
                MaterialTheme.colorScheme.error 
            else 
                MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text)
        }
    }
}

/**
 * Valida si un nombre de archivo es válido
 */
private fun isValidFileName(name: String): Boolean {
    if (name.isBlank()) return false
    val invalidChars = charArrayOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')
    return !name.any { it in invalidChars }
}
