package com.example.practica3.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practica3.R
import com.example.practica3.data.model.FileItem
import com.example.practica3.data.model.ViewType
import com.example.practica3.databinding.ActivityMainBinding
import com.example.practica3.ui.adapter.FileAdapter
import com.example.practica3.ui.viewer.ImageViewerActivity
import com.example.practica3.ui.viewer.TextViewerActivity
import com.example.practica3.utils.FileUtils
import com.example.practica3.utils.PermissionUtils
import com.example.practica3.utils.ThemeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var fileAdapter: FileAdapter

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            viewModel.refreshCurrentDirectory()
        } else {
            showPermissionDeniedDialog()
        }
    }

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (PermissionUtils.hasStoragePermission(this)) {
            viewModel.refreshCurrentDirectory()
        } else {
            showPermissionDeniedDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Apply theme
        viewModel.themeType.observe(this) { themeType ->
            ThemeUtils.applyTheme(this, themeType)
        }
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        
        checkPermissions()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        binding.toolbar.setNavigationOnClickListener {
            if (!viewModel.navigateBack()) {
                finish()
            }
        }
    }

    private fun setupRecyclerView() {
        fileAdapter = FileAdapter(
            viewType = ViewType.LIST,
            onFileClick = { fileItem -> handleFileClick(fileItem) },
            onFileLongClick = { fileItem -> showFileOptionsDialog(fileItem) }
        )

        binding.recyclerViewFiles.adapter = fileAdapter
        updateRecyclerViewLayout(ViewType.LIST)
    }

    private fun setupObservers() {
        viewModel.files.observe(this) { files ->
            fileAdapter.submitList(files)
            binding.layoutEmpty.isVisible = files.isEmpty() && !viewModel.isLoading.value!!
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressLoading.isVisible = isLoading
            binding.swipeRefresh.isRefreshing = isLoading
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.currentPath.observe(this) { path ->
            updateBreadcrumb(path)
            supportActionBar?.subtitle = path
        }

        viewModel.viewType.observe(this) { viewType ->
            fileAdapter.updateViewType(viewType)
            updateRecyclerViewLayout(viewType)
            invalidateOptionsMenu()
        }

        viewModel.isSearchMode.observe(this) { isSearchMode ->
            binding.layoutSearch.isVisible = isSearchMode
            if (!isSearchMode) {
                binding.editTextSearch.text?.clear()
            }
        }
    }

    private fun setupClickListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshCurrentDirectory()
        }

        binding.fabCreateFolder.setOnClickListener {
            showCreateFolderDialog()
        }

        binding.buttonSearch.setOnClickListener {
            toggleSearchMode()
        }

        binding.editTextSearch.setOnEditorActionListener { _, _, _ ->
            val query = binding.editTextSearch.text.toString().trim()
            viewModel.searchFiles(query)
            true
        }
    }

    private fun checkPermissions() {
        if (!PermissionUtils.hasStoragePermission(this)) {
            if (PermissionUtils.shouldShowRequestPermissionRationale(this)) {
                showPermissionRationaleDialog()
            } else {
                requestPermissions()
            }
        }
    }

    private fun requestPermissions() {
        PermissionUtils.requestStoragePermission(this)
    }

    private fun showPermissionRationaleDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.permission_storage_title)
            .setMessage(R.string.permission_storage_message)
            .setPositiveButton(R.string.ok) { _, _ ->
                requestPermissions()
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                finish()
            }
            .show()
    }

    private fun showPermissionDeniedDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.permission_denied)
            .setMessage(R.string.permission_storage_message)
            .setPositiveButton(R.string.permission_settings) { _, _ ->
                PermissionUtils.openAppSettings(this)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                finish()
            }
            .show()
    }

    private fun handleFileClick(fileItem: FileItem) {
        when {
            fileItem.isDirectory -> {
                viewModel.navigateToDirectory(fileItem.path)
            }
            fileItem.isTextFile() -> {
                viewModel.addToRecent(fileItem)
                val intent = Intent(this, TextViewerActivity::class.java)
                intent.putExtra("file_path", fileItem.path)
                startActivity(intent)
            }
            fileItem.isImageFile() -> {
                viewModel.addToRecent(fileItem)
                val intent = Intent(this, ImageViewerActivity::class.java)
                intent.putExtra("file_path", fileItem.path)
                startActivity(intent)
            }
            else -> {
                viewModel.addToRecent(fileItem)
                try {
                    val file = java.io.File(fileItem.path)
                    FileUtils.openFileWithIntent(this, file)
                } catch (e: Exception) {
                    Toast.makeText(this, R.string.error_operation_failed, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showFileOptionsDialog(fileItem: FileItem) {
        val options = mutableListOf<String>()
        options.add(getString(R.string.open_with))
        options.add(getString(R.string.rename))
        options.add(getString(R.string.copy))
        options.add(getString(R.string.move))
        options.add(getString(R.string.share))
        options.add(getString(R.string.delete))

        MaterialAlertDialogBuilder(this)
            .setTitle(fileItem.name)
            .setItems(options.toTypedArray()) { _, which ->
                when (which) {
                    0 -> openFileWithIntent(fileItem)
                    1 -> showRenameDialog(fileItem)
                    2 -> copyFile(fileItem)
                    3 -> moveFile(fileItem)
                    4 -> shareFile(fileItem)
                    5 -> showDeleteConfirmationDialog(fileItem)
                }
            }
            .show()
    }

    private fun openFileWithIntent(fileItem: FileItem) {
        try {
            val file = java.io.File(fileItem.path)
            FileUtils.openFileWithIntent(this, file)
        } catch (e: Exception) {
            Toast.makeText(this, R.string.error_operation_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareFile(fileItem: FileItem) {
        try {
            val file = java.io.File(fileItem.path)
            FileUtils.shareFile(this, file)
        } catch (e: Exception) {
            Toast.makeText(this, R.string.error_operation_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyFile(fileItem: FileItem) {
        // TODO: Implement file operations with proper UI
        Toast.makeText(this, "Función por implementar", Toast.LENGTH_SHORT).show()
    }

    private fun moveFile(fileItem: FileItem) {
        // TODO: Implement file operations with proper UI
        Toast.makeText(this, "Función por implementar", Toast.LENGTH_SHORT).show()
    }

    private fun showRenameDialog(fileItem: FileItem) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_text_input, null)
        val textInputLayout = dialogView.findViewById<TextInputLayout>(R.id.textInputLayout)
        val editText = dialogView.findViewById<TextInputEditText>(R.id.editText)
        
        textInputLayout.hint = getString(R.string.new_name)
        editText.setText(fileItem.name)
        editText.selectAll()

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.rename)
            .setView(dialogView)
            .setPositiveButton(R.string.ok) { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty() && FileUtils.isValidFileName(newName)) {
                    viewModel.renameFile(fileItem, newName)
                } else {
                    Toast.makeText(this, "Nombre de archivo inválido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showDeleteConfirmationDialog(fileItem: FileItem) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.confirm_delete, fileItem.name))
            .setMessage(R.string.delete_confirmation)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteFile(fileItem)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun showCreateFolderDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_text_input, null)
        val textInputLayout = dialogView.findViewById<TextInputLayout>(R.id.textInputLayout)
        val editText = dialogView.findViewById<TextInputEditText>(R.id.editText)
        
        textInputLayout.hint = getString(R.string.folder_name)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.create_folder)
            .setView(dialogView)
            .setPositiveButton(R.string.ok) { _, _ ->
                val folderName = editText.text.toString().trim()
                if (folderName.isNotEmpty() && FileUtils.isValidFileName(folderName)) {
                    viewModel.createFolder(folderName)
                } else {
                    Toast.makeText(this, "Nombre de carpeta inválido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun toggleSearchMode() {
        val isSearchMode = viewModel.isSearchMode.value ?: false
        if (isSearchMode) {
            viewModel.clearSearch()
        } else {
            binding.layoutSearch.isVisible = true
            binding.editTextSearch.requestFocus()
        }
    }

    private fun updateBreadcrumb(path: String) {
        binding.layoutBreadcrumb.removeAllViews()
        
        // TODO: Implement breadcrumb navigation
        // For now, just show the current directory name
        val dirName = java.io.File(path).name.ifEmpty { "Root" }
        supportActionBar?.title = dirName
    }

    private fun updateRecyclerViewLayout(viewType: ViewType) {
        binding.recyclerViewFiles.layoutManager = when (viewType) {
            ViewType.LIST -> LinearLayoutManager(this)
            ViewType.GRID -> GridLayoutManager(this, 2)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val viewType = viewModel.viewType.value ?: ViewType.LIST
        menu.findItem(R.id.action_view_list)?.isVisible = viewType != ViewType.LIST
        menu.findItem(R.id.action_view_grid)?.isVisible = viewType != ViewType.GRID
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_view_list -> {
                viewModel.setViewType(ViewType.LIST)
                true
            }
            R.id.action_view_grid -> {
                viewModel.setViewType(ViewType.GRID)
                true
            }
            R.id.action_sort -> {
                showSortDialog()
                true
            }
            R.id.action_show_hidden -> {
                val showHidden = viewModel.showHiddenFiles.value ?: false
                viewModel.setShowHiddenFiles(!showHidden)
                true
            }
            R.id.action_settings -> {
                showSettingsDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSortDialog() {
        // TODO: Implement sort dialog
        Toast.makeText(this, "Función por implementar", Toast.LENGTH_SHORT).show()
    }

    private fun showSettingsDialog() {
        // TODO: Implement settings dialog
        Toast.makeText(this, "Función por implementar", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (viewModel.isSearchMode.value == true) {
            viewModel.clearSearch()
        } else if (!viewModel.navigateBack()) {
            super.onBackPressed()
        }
    }
}