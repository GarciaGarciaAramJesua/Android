package com.example.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment() {

    // Referencias a elementos TextView
    private lateinit var tvBasic: TextView
    private lateinit var tvStyled: TextView
    private lateinit var tvDynamic: TextView
    private lateinit var tvProgressPercent: TextView
    private lateinit var tvStatus: TextView

    // Referencias a elementos ImageView
    private lateinit var ivIcon1: ImageView
    private lateinit var ivIcon2: ImageView
    private lateinit var ivIcon3: ImageView

    // Referencias a elementos ProgressBar
    private lateinit var progressBarHorizontal: ProgressBar
    private lateinit var progressBarCircular: ProgressBar

    // Referencias a botones
    private lateinit var btnChangeImages: Button
    private lateinit var btnStartProgress: Button
    private lateinit var btnResetProgress: Button

    // Variables para la funcionalidad
    private var currentImageSet = 0
    private var isProgressRunning = false
    private var dynamicTextIndex = 0
    
    // Handlers para animaciones
    private val mainHandler = Handler(Looper.getMainLooper())
    private var progressRunnable: Runnable? = null
    private var dynamicTextRunnable: Runnable? = null

    // Arrays de datos para la demostración
    private val imageIcons = arrayOf(
        arrayOf(
            android.R.drawable.ic_dialog_info,
            android.R.drawable.ic_dialog_alert,
            android.R.drawable.ic_menu_camera
        ),
        arrayOf(
            android.R.drawable.ic_menu_gallery,
            android.R.drawable.ic_menu_preferences,
            android.R.drawable.ic_menu_save
        ),
        arrayOf(
            android.R.drawable.ic_menu_search,
            android.R.drawable.ic_menu_share,
            android.R.drawable.ic_menu_delete
        )
    )

    private val dynamicTexts = arrayOf(
        "🔄 Texto actualizado: ${getCurrentTime()}",
        "✨ Contenido dinámico en acción",
        "📱 TextView puede cambiar automáticamente",
        "🎯 Perfecto para actualizaciones en tiempo real",
        "💫 Ideal para mostrar información dinámica"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        
        initializeViews(view)
        setupInteractions()
        startDynamicTextAnimation()
        
        return view
    }

    private fun initializeViews(view: View) {
        // Inicializar TextViews
        tvBasic = view.findViewById(R.id.tvBasic)
        tvStyled = view.findViewById(R.id.tvStyled)
        tvDynamic = view.findViewById(R.id.tvDynamic)
        tvProgressPercent = view.findViewById(R.id.tvProgressPercent)
        tvStatus = view.findViewById(R.id.tvStatus)

        // Inicializar ImageViews
        ivIcon1 = view.findViewById(R.id.ivIcon1)
        ivIcon2 = view.findViewById(R.id.ivIcon2)
        ivIcon3 = view.findViewById(R.id.ivIcon3)

        // Inicializar ProgressBars
        progressBarHorizontal = view.findViewById(R.id.progressBarHorizontal)
        progressBarCircular = view.findViewById(R.id.progressBarCircular)

        // Inicializar botones
        btnChangeImages = view.findViewById(R.id.btnChangeImages)
        btnStartProgress = view.findViewById(R.id.btnStartProgress)
        btnResetProgress = view.findViewById(R.id.btnResetProgress)
    }

    private fun setupInteractions() {
        // Configurar click en TextViews para mostrar Toast
        tvBasic.setOnClickListener {
            Toast.makeText(context, "TextView básico presionado", Toast.LENGTH_SHORT).show()
        }

        tvStyled.setOnClickListener {
            Toast.makeText(context, "TextView estilizado presionado", Toast.LENGTH_SHORT).show()
        }

        // Configurar botón para cambiar imágenes
        btnChangeImages.setOnClickListener {
            changeImageSet()
        }

        // Configurar botón para iniciar progreso
        btnStartProgress.setOnClickListener {
            startProgressAnimation()
        }

        // Configurar botón para resetear progreso
        btnResetProgress.setOnClickListener {
            resetProgress()
        }

        // Configurar clicks en ImageViews
        ivIcon1.setOnClickListener {
            Toast.makeText(context, "Imagen 1 presionada", Toast.LENGTH_SHORT).show()
        }

        ivIcon2.setOnClickListener {
            Toast.makeText(context, "Imagen 2 presionada", Toast.LENGTH_SHORT).show()
        }

        ivIcon3.setOnClickListener {
            Toast.makeText(context, "Imagen 3 presionada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startDynamicTextAnimation() {
        dynamicTextRunnable = object : Runnable {
            override fun run() {
                updateDynamicText()
                mainHandler.postDelayed(this, 2000) // Actualizar cada 2 segundos
            }
        }
        mainHandler.post(dynamicTextRunnable!!)
    }

    private fun updateDynamicText() {
        val text = if (dynamicTextIndex == 0) {
            "🔄 Texto actualizado: ${getCurrentTime()}"
        } else {
            dynamicTexts[dynamicTextIndex]
        }
        
        tvDynamic.text = text
        dynamicTextIndex = (dynamicTextIndex + 1) % dynamicTexts.size
    }

    private fun changeImageSet() {
        currentImageSet = (currentImageSet + 1) % imageIcons.size
        
        ivIcon1.setImageResource(imageIcons[currentImageSet][0])
        ivIcon2.setImageResource(imageIcons[currentImageSet][1])
        ivIcon3.setImageResource(imageIcons[currentImageSet][2])
        
        updateStatus("🖼️ Conjunto de imágenes cambiado (Set ${currentImageSet + 1})")
        Toast.makeText(context, "Imágenes cambiadas al conjunto ${currentImageSet + 1}", Toast.LENGTH_SHORT).show()
    }

    private fun startProgressAnimation() {
        if (isProgressRunning) {
            Toast.makeText(context, "El progreso ya está en ejecución", Toast.LENGTH_SHORT).show()
            return
        }

        isProgressRunning = true
        btnStartProgress.isEnabled = false
        progressBarCircular.visibility = View.VISIBLE
        
        progressRunnable = object : Runnable {
            override fun run() {
                val currentProgress = progressBarHorizontal.progress
                if (currentProgress < 100) {
                    val newProgress = currentProgress + 2
                    progressBarHorizontal.progress = newProgress
                    tvProgressPercent.text = "$newProgress%"
                    updateStatus("⏳ Progreso en curso: $newProgress%")
                    mainHandler.postDelayed(this, 50) // Actualizar cada 50ms
                } else {
                    // Progreso completado
                    isProgressRunning = false
                    btnStartProgress.isEnabled = true
                    progressBarCircular.visibility = View.GONE
                    updateStatus("✅ Progreso completado al 100%")
                    Toast.makeText(context, "¡Progreso completado!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        mainHandler.post(progressRunnable!!)
        updateStatus("▶️ Iniciando animación de progreso...")
    }

    private fun resetProgress() {
        // Detener animación si está corriendo
        progressRunnable?.let {
            mainHandler.removeCallbacks(it)
        }
        
        isProgressRunning = false
        btnStartProgress.isEnabled = true
        progressBarCircular.visibility = View.GONE
        progressBarHorizontal.progress = 0
        tvProgressPercent.text = "0%"
        
        updateStatus("🔄 Progreso reseteado a 0%")
        Toast.makeText(context, "Progreso reseteado", Toast.LENGTH_SHORT).show()
    }

    private fun updateStatus(message: String) {
        tvStatus.text = "📋 Estado: $message"
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpiar handlers para evitar memory leaks
        dynamicTextRunnable?.let {
            mainHandler.removeCallbacks(it)
        }
        progressRunnable?.let {
            mainHandler.removeCallbacks(it)
        }
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}
