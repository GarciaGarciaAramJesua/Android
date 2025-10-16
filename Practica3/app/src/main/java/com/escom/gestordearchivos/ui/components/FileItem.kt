package com.escom.gestordearchivos.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.escom.gestordearchivos.domain.FileItem
import com.escom.gestordearchivos.domain.FileType
import com.escom.gestordearchivos.utils.FileUtils

/**
 * Componente que muestra un archivo en vista de lista
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileListItem(
    fileItem: FileItem,
    isFavorite: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del archivo
            FileIconOrThumbnail(
                fileItem = fileItem,
                size = 48.dp
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Información del archivo
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = fileItem.name,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (isFavorite) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Favorito",
                            modifier = Modifier
                                .size(16.dp)
                                .padding(start = 4.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = fileItem.getFormattedSize(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = fileItem.getFormattedDate(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Componente que muestra un archivo en vista de cuadrícula
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileGridItem(
    fileItem: FileItem,
    isFavorite: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(0.8f)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                FileIconOrThumbnail(
                    fileItem = fileItem,
                    size = 64.dp
                )
                if (isFavorite) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Favorito",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = fileItem.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = fileItem.getFormattedSize(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Muestra un icono o miniatura del archivo
 */
@Composable
fun FileIconOrThumbnail(
    fileItem: FileItem,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    when (fileItem.getFileType()) {
        FileType.IMAGE -> {
            // Mostrar miniatura de imagen
            AsyncImage(
                model = fileItem.file,
                contentDescription = fileItem.name,
                modifier = modifier
                    .size(size)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
        else -> {
            // Mostrar icono según tipo
            Icon(
                imageVector = FileUtils.getFileIcon(fileItem.getFileType()),
                contentDescription = fileItem.name,
                modifier = modifier.size(size),
                tint = when (fileItem.getFileType()) {
                    FileType.DIRECTORY -> MaterialTheme.colorScheme.primary
                    FileType.IMAGE -> MaterialTheme.colorScheme.tertiary
                    FileType.VIDEO -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
