package com.example.practica3.ui.viewer

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.practica3.R
import com.example.practica3.databinding.ActivityTextViewerBinding
import java.io.File

class TextViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTextViewerBinding
    private var filePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadFile()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadFile() {
        filePath = intent.getStringExtra("file_path")
        if (filePath == null) {
            Toast.makeText(this, R.string.error_file_not_found, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val file = File(filePath!!)
        supportActionBar?.title = file.name

        try {
            if (!file.exists() || !file.canRead()) {
                Toast.makeText(this, R.string.error_access_denied, Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            val content = file.readText()
            binding.textViewContent.text = content
            
            // Show file info
            val lineCount = content.count { it == '\n' } + 1
            supportActionBar?.subtitle = getString(R.string.line_count, lineCount)

        } catch (e: Exception) {
            Toast.makeText(this, R.string.error_corrupted_file, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_text_viewer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                shareFile()
                true
            }
            R.id.action_open_with -> {
                openWithExternalApp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun shareFile() {
        filePath?.let { path ->
            try {
                val file = File(path)
                com.example.practica3.utils.FileUtils.shareFile(this, file)
            } catch (e: Exception) {
                Toast.makeText(this, R.string.error_operation_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openWithExternalApp() {
        filePath?.let { path ->
            try {
                val file = File(path)
                com.example.practica3.utils.FileUtils.openFileWithIntent(this, file)
            } catch (e: Exception) {
                Toast.makeText(this, R.string.error_operation_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }
}