package com.example.practica2

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat

/**
 * Activity base que aplica el tema automáticamente
 * Todas las activities deben heredar de esta clase
 */
open class BaseActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // IMPORTANTE: Aplicar el tema ANTES de super.onCreate()
        ThemeManager.applyTheme(this)
        super.onCreate(savedInstanceState)
        
        // Aplicar colores adicionales después de setContentView()
        // (esto se hará en cada activity específica después de setContentView)
    }

    /**
     * Aplica colores del tema a las vistas después de setContentView()
     */
    protected fun applyThemeColors() {
        // Obtener colores del tema actual
        val backgroundColor = getThemeColor(android.R.attr.colorBackground)
        val textColorPrimary = getThemeColor(android.R.attr.textColorPrimary)
        val textColorSecondary = getThemeColor(android.R.attr.textColorSecondary)
        
        // Aplicar a la vista raíz si existe
        findViewById<android.view.View>(R.id.main)?.setBackgroundColor(backgroundColor)
    }
    
    /**
     * Obtiene un color del tema actual
     */
    protected fun getThemeColor(attr: Int): Int {
        val typedValue = android.util.TypedValue()
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }
    
    /**
     * Aplica el tema a un TextView específico
     */
    protected fun applyTextColor(textView: TextView?, isPrimary: Boolean = true) {
        textView?.setTextColor(
            getThemeColor(
                if (isPrimary) android.R.attr.textColorPrimary 
                else android.R.attr.textColorSecondary
            )
        )
    }
    
    /**
     * Aplica el tema a una CardView
     */
    protected fun applyCardTheme(cardView: CardView?) {
        cardView?.setCardBackgroundColor(
            ContextCompat.getColor(this,
                if (ThemeManager.isDarkMode(this)) R.color.dark_surface 
                else R.color.light_surface
            )
        )
    }
}