package com.example.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment

class SearchFragment : Fragment() {

    // Referencias a los elementos de la UI
    private lateinit var btnBasic: Button
    private lateinit var btnStyled: Button
    private lateinit var btnText: Button
    private lateinit var imgBtnHeart: ImageButton
    private lateinit var imgBtnStar: ImageButton
    private lateinit var imgBtnShare: ImageButton
    private lateinit var btnIncrement: Button
    private lateinit var btnDecrement: Button
    private lateinit var btnReset: Button
    private lateinit var tvCounter: TextView
    private lateinit var tvResults: TextView

    // Variables para la demostración
    private var counter = 0
    private var totalClicks = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        
        initializeViews(view)
        setupInteractions()
        updateDisplay()
        
        return view
    }

    private fun initializeViews(view: View) {
        btnBasic = view.findViewById(R.id.btnBasic)
        btnStyled = view.findViewById(R.id.btnStyled)
        btnText = view.findViewById(R.id.btnText)
        imgBtnHeart = view.findViewById(R.id.imgBtnHeart)
        imgBtnStar = view.findViewById(R.id.imgBtnStar)
        imgBtnShare = view.findViewById(R.id.imgBtnShare)
        btnIncrement = view.findViewById(R.id.btnIncrement)
        btnDecrement = view.findViewById(R.id.btnDecrement)
        btnReset = view.findViewById(R.id.btnReset)
        tvCounter = view.findViewById(R.id.tvCounter)
        tvResults = view.findViewById(R.id.tvResults)
    }

    private fun setupInteractions() {
        // Botones estándar
        btnBasic.setOnClickListener {
            totalClicks++
            showResult("Botón Básico presionado")
        }

        btnStyled.setOnClickListener {
            totalClicks++
            showResult("Botón Estilizado presionado")
        }

        btnText.setOnClickListener {
            totalClicks++
            showResult("Botón de Texto presionado")
        }

        // ImageButtons
        imgBtnHeart.setOnClickListener {
            totalClicks++
            showResult("❤️ ImageButton Corazón presionado")
        }

        imgBtnStar.setOnClickListener {
            totalClicks++
            showResult("⭐ ImageButton Estrella presionado")
        }

        imgBtnShare.setOnClickListener {
            totalClicks++
            showResult("📤 ImageButton Compartir presionado")
        }

        // Contador interactivo
        btnIncrement.setOnClickListener {
            counter++
            totalClicks++
            updateDisplay()
            showResult("Contador incrementado a $counter")
        }

        btnDecrement.setOnClickListener {
            counter--
            totalClicks++
            updateDisplay()
            showResult("Contador decrementado a $counter")
        }

        btnReset.setOnClickListener {
            counter = 0
            totalClicks++
            updateDisplay()
            showResult("🔄 Contador reseteado")
        }
    }

    private fun updateDisplay() {
        tvCounter.text = counter.toString()
        
        // Cambiar color del contador según el valor
        when {
            counter > 0 -> tvCounter.setBackgroundColor(resources.getColor(android.R.color.holo_green_light, null))
            counter < 0 -> tvCounter.setBackgroundColor(resources.getColor(android.R.color.holo_red_light, null))
            else -> tvCounter.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light, null))
        }
    }

    private fun showResult(message: String) {
        val resultText = "🎯 $message\n\n" +
                "📊 Estadísticas:\n" +
                "• Contador actual: $counter\n" +
                "• Total de clics: $totalClicks\n\n" +
                "💡 ¡Sigue tocando botones para experimentar!"
        
        tvResults.text = resultText
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}
