package com.example.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment

class NotificationsFragment : Fragment() {

    // Referencias a CheckBoxes
    private lateinit var cbEmail: CheckBox
    private lateinit var cbPush: CheckBox
    private lateinit var cbSms: CheckBox

    // Referencias a RadioGroup y RadioButtons
    private lateinit var rgFrequency: RadioGroup
    private lateinit var rbInstant: RadioButton
    private lateinit var rbHourly: RadioButton
    private lateinit var rbDaily: RadioButton
    private lateinit var rbWeekly: RadioButton

    // Referencias a Switches
    private lateinit var swNotifications: Switch
    private lateinit var swVibration: Switch
    private lateinit var swSound: Switch

    // Referencias a botones y TextView
    private lateinit var btnShowPreferences: Button
    private lateinit var btnResetAll: Button
    private lateinit var tvResults: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)
        
        initializeViews(view)
        setupInteractions()
        
        return view
    }

    private fun initializeViews(view: View) {
        // Inicializar CheckBoxes
        cbEmail = view.findViewById(R.id.cbEmail)
        cbPush = view.findViewById(R.id.cbPush)
        cbSms = view.findViewById(R.id.cbSms)

        // Inicializar RadioGroup y RadioButtons
        rgFrequency = view.findViewById(R.id.rgFrequency)
        rbInstant = view.findViewById(R.id.rbInstant)
        rbHourly = view.findViewById(R.id.rbHourly)
        rbDaily = view.findViewById(R.id.rbDaily)
        rbWeekly = view.findViewById(R.id.rbWeekly)

        // Inicializar Switches
        swNotifications = view.findViewById(R.id.swNotifications)
        swVibration = view.findViewById(R.id.swVibration)
        swSound = view.findViewById(R.id.swSound)

        // Inicializar botones y TextView
        btnShowPreferences = view.findViewById(R.id.btnShowPreferences)
        btnResetAll = view.findViewById(R.id.btnResetAll)
        tvResults = view.findViewById(R.id.tvResults)
    }

    private fun setupInteractions() {
        // Configurar listeners para CheckBoxes
        cbEmail.setOnCheckedChangeListener { _, isChecked ->
            updateResultsDisplay()
        }
        
        cbPush.setOnCheckedChangeListener { _, isChecked ->
            updateResultsDisplay()
        }
        
        cbSms.setOnCheckedChangeListener { _, isChecked ->
            updateResultsDisplay()
        }

        // Configurar listener para RadioGroup
        rgFrequency.setOnCheckedChangeListener { _, checkedId ->
            updateResultsDisplay()
        }

        // Configurar listeners para Switches
        swNotifications.setOnCheckedChangeListener { _, isChecked ->
            // Cuando se desactivan las notificaciones generales, desactivar todo
            if (!isChecked) {
                swVibration.isChecked = false
                swSound.isChecked = false
                cbEmail.isChecked = false
                cbPush.isChecked = false
                cbSms.isChecked = false
            }
            updateResultsDisplay()
        }

        swVibration.setOnCheckedChangeListener { _, isChecked ->
            updateResultsDisplay()
        }

        swSound.setOnCheckedChangeListener { _, isChecked ->
            updateResultsDisplay()
        }

        // Configurar botones
        btnShowPreferences.setOnClickListener {
            showDetailedPreferences()
        }

        btnResetAll.setOnClickListener {
            resetAllPreferences()
        }
    }

    private fun updateResultsDisplay() {
        if (tvResults.visibility == View.VISIBLE) {
            showDetailedPreferences()
        }
    }

    private fun showDetailedPreferences() {
        val results = StringBuilder()
        results.append("🔧 Configuración Actual de Notificaciones:\n\n")

        // Mostrar estado de las notificaciones generales
        results.append("📱 Estado General:\n")
        results.append("• Notificaciones: ${if (swNotifications.isChecked) "✅ Activadas" else "❌ Desactivadas"}\n")
        results.append("• Vibración: ${if (swVibration.isChecked) "✅ Activada" else "❌ Desactivada"}\n")
        results.append("• Sonido: ${if (swSound.isChecked) "✅ Activado" else "❌ Desactivado"}\n\n")

        // Mostrar tipos de notificaciones seleccionadas
        results.append("📬 Tipos de Notificaciones:\n")
        val selectedTypes = mutableListOf<String>()
        if (cbEmail.isChecked) selectedTypes.add("📧 Email")
        if (cbPush.isChecked) selectedTypes.add("📱 Push")
        if (cbSms.isChecked) selectedTypes.add("💬 SMS")

        if (selectedTypes.isEmpty()) {
            results.append("• Ningún tipo seleccionado\n")
        } else {
            selectedTypes.forEach { type ->
                results.append("• $type\n")
            }
        }

        // Mostrar frecuencia seleccionada
        results.append("\n⏰ Frecuencia de Notificaciones:\n")
        val selectedFrequency = when (rgFrequency.checkedRadioButtonId) {
            R.id.rbInstant -> "⚡ Instantáneas"
            R.id.rbHourly -> "🕐 Cada hora"
            R.id.rbDaily -> "📅 Diarias"
            R.id.rbWeekly -> "📊 Semanales"
            else -> "❓ No seleccionada"
        }
        results.append("• $selectedFrequency\n\n")

        // Agregar resumen
        val activeCount = listOf(cbEmail.isChecked, cbPush.isChecked, cbSms.isChecked).count { it }
        results.append("📊 Resumen: $activeCount tipos de notificación activos")

        tvResults.text = results.toString()
        tvResults.visibility = View.VISIBLE
    }

    private fun resetAllPreferences() {
        // Resetear CheckBoxes
        cbEmail.isChecked = false
        cbPush.isChecked = false
        cbSms.isChecked = false

        // Resetear RadioGroup a opción por defecto
        rbDaily.isChecked = true

        // Resetear Switches a estado por defecto
        swNotifications.isChecked = true
        swVibration.isChecked = false
        swSound.isChecked = false

        // Ocultar resultados
        tvResults.visibility = View.GONE
    }

    companion object {
        fun newInstance() = NotificationsFragment()
    }
}
