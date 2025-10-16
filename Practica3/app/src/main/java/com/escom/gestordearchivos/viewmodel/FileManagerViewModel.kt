package com.escom.gestordearchivos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.escom.gestordearchivos.data.database.FavoriteFileEntity
import com.escom.gestordearchivos.data.database.RecentFileEntity
import com.escom.gestordearchivos.data.repository.FileRepository
import com.escom.gestordearchivos.domain.FileItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

/**
 * ViewModel principal que gestiona el estado de la aplicación
 * Implementa el patrón MVVM para separar la lógica de negocio de la UI
 */
class FileManagerViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = FileRepository(application)

    // Estado de la navegación actual
    private val _currentDirectory = MutableStateFlow<File>(repository.getRootDirectory())
    val currentDirectory: StateFlow<File> = _currentDirectory.asStateFlow()

    // Lista de archivos en el directorio actual
    private val _fileList = MutableStateFlow<List<FileItem>>(emptyList())
    val fileList: StateFlow<List<FileItem>> = _fileList.asStateFlow()

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Mensajes de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Pila de navegación para el botón atrás
    private val navigationStack = mutableListOf<File>()

    // Archivos recientes y favoritos
    val recentFiles: Flow<List<RecentFileEntity>> = repository.getRecentFiles()
    val favorites: Flow<List<FavoriteFileEntity>> = repository.getFavorites()

    // Estado de búsqueda
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<FileItem>>(emptyList())
    val searchResults: StateFlow<List<FileItem>> = _searchResults.asStateFlow()

    // Vista de lista o cuadrícula
    private val _isGridView = MutableStateFlow(false)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

    init {
        loadFiles(repository.getRootDirectory())
    }

    /**
     * Obtiene el directorio raíz
     */
    fun getRootDirectory(): File = repository.getRootDirectory()

    /**
     * Carga los archivos del directorio especificado
     */
    fun loadFiles(directory: File) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.listFiles(directory).fold(
                onSuccess = { files ->
                    _fileList.value = files
                    _currentDirectory.value = directory
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error al cargar archivos"
                }
            )

            _isLoading.value = false
        }
    }

    /**
     * Navega a un directorio hijo
     */
    fun navigateToDirectory(directory: File) {
        if (directory.isDirectory) {
            navigationStack.add(_currentDirectory.value)
            loadFiles(directory)
        }
    }

    /**
     * Navega al directorio padre
     */
    fun navigateUp(): Boolean {
        val parent = _currentDirectory.value.parentFile
        return if (parent != null && parent.canRead()) {
            if (navigationStack.isNotEmpty()) {
                navigationStack.removeAt(navigationStack.lastIndex)
            }
            loadFiles(parent)
            true
        } else {
            false
        }
    }

    /**
     * Navega atrás en el historial
     */
    fun navigateBack(): Boolean {
        return if (navigationStack.isNotEmpty()) {
            val previousDir = navigationStack.removeAt(navigationStack.lastIndex)
            loadFiles(previousDir)
            true
        } else {
            false
        }
    }

    /**
     * Busca archivos por nombre
     */
    fun searchFiles(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            repository.searchFiles(_currentDirectory.value, query).fold(
                onSuccess = { results ->
                    _searchResults.value = results
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Error en la búsqueda"
                }
            )
            _isLoading.value = false
        }
    }

    /**
     * Limpia la búsqueda
     */
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }

    /**
     * Crea una nueva carpeta
     */
    fun createFolder(folderName: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            repository.createFolder(_currentDirectory.value, folderName).fold(
                onSuccess = {
                    loadFiles(_currentDirectory.value)
                    onSuccess()
                },
                onFailure = { exception ->
                    onError(exception.message ?: "Error al crear carpeta")
                }
            )
        }
    }

    /**
     * Renombra un archivo
     */
    fun renameFile(file: File, newName: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            repository.renameFile(file, newName).fold(
                onSuccess = {
                    loadFiles(_currentDirectory.value)
                    onSuccess()
                },
                onFailure = { exception ->
                    onError(exception.message ?: "Error al renombrar")
                }
            )
        }
    }

    /**
     * Copia un archivo
     */
    fun copyFile(source: File, destination: File, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            repository.copyFile(source, destination).fold(
                onSuccess = {
                    loadFiles(_currentDirectory.value)
                    onSuccess()
                },
                onFailure = { exception ->
                    onError(exception.message ?: "Error al copiar")
                }
            )
        }
    }

    /**
     * Mueve un archivo
     */
    fun moveFile(source: File, destination: File, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            repository.moveFile(source, destination).fold(
                onSuccess = {
                    loadFiles(_currentDirectory.value)
                    onSuccess()
                },
                onFailure = { exception ->
                    onError(exception.message ?: "Error al mover")
                }
            )
        }
    }

    /**
     * Elimina un archivo
     */
    fun deleteFile(file: File, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            repository.deleteFile(file).fold(
                onSuccess = {
                    loadFiles(_currentDirectory.value)
                    onSuccess()
                },
                onFailure = { exception ->
                    onError(exception.message ?: "Error al eliminar")
                }
            )
        }
    }

    /**
     * Agrega un archivo al historial de recientes
     */
    fun addToRecent(fileItem: FileItem) {
        viewModelScope.launch {
            repository.addRecentFile(fileItem)
        }
    }

    /**
     * Agrega o quita un archivo de favoritos
     */
    fun toggleFavorite(fileItem: FileItem) {
        viewModelScope.launch {
            if (repository.isFavorite(fileItem.path)) {
                repository.removeFavorite(fileItem.path)
            } else {
                repository.addFavorite(fileItem)
            }
        }
    }

    /**
     * Verifica si un archivo es favorito
     */
    suspend fun isFavorite(path: String): Boolean {
        return repository.isFavorite(path)
    }

    /**
     * Alterna entre vista de lista y cuadrícula
     */
    fun toggleViewMode() {
        _isGridView.value = !_isGridView.value
    }

    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
