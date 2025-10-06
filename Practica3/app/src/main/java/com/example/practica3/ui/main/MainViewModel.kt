package com.example.practica3.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.example.practica3.FileExplorerApplication
import com.example.practica3.data.model.*
import com.example.practica3.data.repository.FileRepository
import com.example.practica3.data.preferences.PreferencesManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val app = application as FileExplorerApplication
    private val fileRepository: FileRepository = app.fileRepository
    private val preferencesManager: PreferencesManager = app.preferencesManager

    private val _currentPath = MutableLiveData<String>()
    val currentPath: LiveData<String> = _currentPath

    private val _files = MutableLiveData<List<FileItem>>()
    val files: LiveData<List<FileItem>> = _files

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    private val _isSearchMode = MutableLiveData<Boolean>()
    val isSearchMode: LiveData<Boolean> = _isSearchMode

    // Navigation history
    private val _navigationStack = mutableListOf<String>()
    val navigationStack: List<String> = _navigationStack

    // Preferences
    val viewType: LiveData<ViewType> = preferencesManager.viewType.asLiveData()
    val sortType: LiveData<SortType> = preferencesManager.sortType.asLiveData()
    val showHiddenFiles: LiveData<Boolean> = preferencesManager.showHiddenFiles.asLiveData()
    val themeType: LiveData<ThemeType> = preferencesManager.themeType.asLiveData()

    // Recent and favorite files
    val recentFiles: LiveData<List<RecentFile>> = fileRepository.getRecentFiles().asLiveData()
    val favoriteFiles: LiveData<List<FavoriteFile>> = fileRepository.getFavoriteFiles().asLiveData()

    init {
        _isLoading.value = false
        _isSearchMode.value = false
        
        // Load last directory or default
        viewModelScope.launch {
            val lastDir = preferencesManager.lastDirectory.first()
            val startPath = lastDir ?: fileRepository.getInternalStoragePath()
            navigateToDirectory(startPath)
        }
    }

    fun navigateToDirectory(path: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _isSearchMode.value = false

            try {
                val result = fileRepository.getFilesInDirectory(path)
                result.fold(
                    onSuccess = { fileList ->
                        val filteredFiles = filterAndSortFiles(fileList)
                        _files.value = filteredFiles
                        _currentPath.value = path
                        
                        // Add to navigation stack
                        if (_navigationStack.isEmpty() || _navigationStack.last() != path) {
                            _navigationStack.add(path)
                        }
                        
                        // Save current directory
                        preferencesManager.setLastDirectory(path)
                    },
                    onFailure = { exception ->
                        _error.value = exception.message
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun navigateUp() {
        val currentDir = _currentPath.value ?: return
        val parentDir = File(currentDir).parent
        if (parentDir != null) {
            navigateToDirectory(parentDir)
        }
    }

    fun navigateBack(): Boolean {
        return if (_navigationStack.size > 1) {
            _navigationStack.removeLastOrNull()
            val previousPath = _navigationStack.lastOrNull()
            if (previousPath != null) {
                navigateToDirectory(previousPath)
                true
            } else {
                false
            }
        } else {
            false
        }
    }

    fun refreshCurrentDirectory() {
        val currentDir = _currentPath.value
        if (currentDir != null) {
            navigateToDirectory(currentDir)
        }
    }

    fun searchFiles(query: String) {
        if (query.isBlank()) {
            _isSearchMode.value = false
            refreshCurrentDirectory()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _isSearchMode.value = true
            _searchQuery.value = query

            try {
                val searchPath = _currentPath.value ?: fileRepository.getInternalStoragePath()
                val result = fileRepository.searchFiles(query, searchPath)
                result.fold(
                    onSuccess = { searchResults ->
                        val filteredFiles = filterAndSortFiles(searchResults)
                        _files.value = filteredFiles
                    },
                    onFailure = { exception ->
                        _error.value = exception.message
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSearch() {
        _isSearchMode.value = false
        _searchQuery.value = ""
        refreshCurrentDirectory()
    }

    private fun filterAndSortFiles(files: List<FileItem>): List<FileItem> {
        val showHidden = showHiddenFiles.value ?: false
        val sort = sortType.value ?: SortType.NAME_ASC
        
        var filteredFiles = if (showHidden) files else files.filter { !it.isHidden }
        
        filteredFiles = when (sort) {
            SortType.NAME_ASC -> filteredFiles.sortedWith(compareBy<FileItem> { !it.isDirectory }.thenBy { it.name.lowercase() })
            SortType.NAME_DESC -> filteredFiles.sortedWith(compareBy<FileItem> { !it.isDirectory }.thenByDescending { it.name.lowercase() })
            SortType.SIZE_ASC -> filteredFiles.sortedWith(compareBy<FileItem> { !it.isDirectory }.thenBy { it.size })
            SortType.SIZE_DESC -> filteredFiles.sortedWith(compareBy<FileItem> { !it.isDirectory }.thenByDescending { it.size })
            SortType.DATE_ASC -> filteredFiles.sortedWith(compareBy<FileItem> { !it.isDirectory }.thenBy { it.lastModified })
            SortType.DATE_DESC -> filteredFiles.sortedWith(compareBy<FileItem> { !it.isDirectory }.thenByDescending { it.lastModified })
            SortType.TYPE_ASC -> filteredFiles.sortedWith(compareBy<FileItem> { !it.isDirectory }.thenBy { it.mimeType })
            SortType.TYPE_DESC -> filteredFiles.sortedWith(compareBy<FileItem> { !it.isDirectory }.thenByDescending { it.mimeType })
        }
        
        return filteredFiles
    }

    // File operations
    fun createFolder(folderName: String) {
        val currentDir = _currentPath.value ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = fileRepository.createFolder(currentDir, folderName)
                result.fold(
                    onSuccess = {
                        refreshCurrentDirectory()
                    },
                    onFailure = { exception ->
                        _error.value = exception.message
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteFile(fileItem: FileItem) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = fileRepository.deleteFile(fileItem.path)
                result.fold(
                    onSuccess = {
                        refreshCurrentDirectory()
                    },
                    onFailure = { exception ->
                        _error.value = exception.message
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun renameFile(fileItem: FileItem, newName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = fileRepository.renameFile(fileItem.path, newName)
                result.fold(
                    onSuccess = {
                        refreshCurrentDirectory()
                    },
                    onFailure = { exception ->
                        _error.value = exception.message
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToRecent(fileItem: FileItem) {
        viewModelScope.launch {
            fileRepository.addRecentFile(fileItem)
        }
    }

    fun addToFavorites(fileItem: FileItem) {
        viewModelScope.launch {
            fileRepository.addFavorite(fileItem)
        }
    }

    fun removeFromFavorites(path: String) {
        viewModelScope.launch {
            fileRepository.removeFavorite(path)
        }
    }

    suspend fun isFavorite(path: String): Boolean {
        return fileRepository.isFavorite(path)
    }

    // Preferences
    fun setViewType(viewType: ViewType) {
        viewModelScope.launch {
            preferencesManager.setViewType(viewType)
        }
    }

    fun setSortType(sortType: SortType) {
        viewModelScope.launch {
            preferencesManager.setSortType(sortType)
        }
    }

    fun setShowHiddenFiles(show: Boolean) {
        viewModelScope.launch {
            preferencesManager.setShowHiddenFiles(show)
            refreshCurrentDirectory()
        }
    }

    fun setThemeType(themeType: ThemeType) {
        viewModelScope.launch {
            preferencesManager.setThemeType(themeType)
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun getStoragePaths(): List<String> {
        val paths = mutableListOf<String>()
        paths.add(fileRepository.getInternalStoragePath())
        paths.addAll(fileRepository.getExternalStoragePaths())
        return paths.distinct()
    }
}