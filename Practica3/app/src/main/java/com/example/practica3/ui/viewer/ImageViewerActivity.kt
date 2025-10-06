package com.example.practica3.ui.viewer

import android.graphics.Matrix
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.practica3.R
import com.example.practica3.databinding.ActivityImageViewerBinding
import java.io.File

class ImageViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageViewerBinding
    private var filePath: String? = null
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f
    private var rotation = 0f
    private val matrix = Matrix()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupImageView()
        loadImage()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupImageView() {
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
        
        binding.imageView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            
            when (event.action) {
                MotionEvent.ACTION_DOWN -> true
                MotionEvent.ACTION_MOVE -> {
                    if (!scaleGestureDetector.isInProgress) {
                        // Handle pan gesture
                        val deltaX = event.x - event.historyX
                        val deltaY = event.y - event.historyY
                        matrix.postTranslate(deltaX, deltaY)
                        binding.imageView.imageMatrix = matrix
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun loadImage() {
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

            Glide.with(this)
                .load(file)
                .error(R.drawable.ic_image)
                .into(binding.imageView)

            // Show file info
            val sizeText = com.example.practica3.utils.FileUtils.formatFileSize(file.length())
            supportActionBar?.subtitle = sizeText

        } catch (e: Exception) {
            Toast.makeText(this, R.string.error_corrupted_file, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_image_viewer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_zoom_in -> {
                zoomIn()
                true
            }
            R.id.action_zoom_out -> {
                zoomOut()
                true
            }
            R.id.action_rotate_left -> {
                rotateLeft()
                true
            }
            R.id.action_rotate_right -> {
                rotateRight()
                true
            }
            R.id.action_reset -> {
                resetImage()
                true
            }
            R.id.action_share -> {
                shareImage()
                true
            }
            R.id.action_open_with -> {
                openWithExternalApp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun zoomIn() {
        scaleFactor *= 1.2f
        updateImageMatrix()
    }

    private fun zoomOut() {
        scaleFactor /= 1.2f
        scaleFactor = maxOf(0.1f, scaleFactor) // Minimum scale
        updateImageMatrix()
    }

    private fun rotateLeft() {
        rotation -= 90f
        updateImageMatrix()
    }

    private fun rotateRight() {
        rotation += 90f
        updateImageMatrix()
    }

    private fun resetImage() {
        scaleFactor = 1.0f
        rotation = 0f
        matrix.reset()
        binding.imageView.imageMatrix = matrix
    }

    private fun updateImageMatrix() {
        matrix.reset()
        matrix.postScale(scaleFactor, scaleFactor)
        matrix.postRotate(rotation)
        binding.imageView.imageMatrix = matrix
    }

    private fun shareImage() {
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

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = maxOf(0.1f, minOf(scaleFactor, 10.0f)) // Limit scale
            updateImageMatrix()
            return true
        }
    }
}