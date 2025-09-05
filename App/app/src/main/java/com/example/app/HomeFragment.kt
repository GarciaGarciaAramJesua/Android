package com.example.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    // Referencias a los elementos de la UI
    private lateinit var etBasic: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etNumber: EditText
    private lateinit var btnShowValues: Button
    private lateinit var btnClearAll: Button
    private lateinit var tvResults: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        
        initializeViews(view)
        setupInteractions()
        
        return view
    }

    private fun initializeViews(view: View) {
        etBasic = view.findViewById(R.id.etBasic)
        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        etNumber = view.findViewById(R.id.etNumber)
        btnShowValues = view.findViewById(R.id.btnShowValues)
        btnClearAll = view.findViewById(R.id.btnClearAll)
        tvResults = view.findViewById(R.id.tvResults)
    }

    private fun setupInteractions() {
        btnShowValues.setOnClickListener {
            showCurrentValues()
        }

        btnClearAll.setOnClickListener {
            clearAllFields()
        }
    }

    private fun showCurrentValues() {
        val basicText = etBasic.text.toString().trim()
        val emailText = etEmail.text.toString().trim()
        val passwordText = etPassword.text.toString().trim()
        val numberText = etNumber.text.toString().trim()

        val results = StringBuilder()
        results.append("📋 Valores Ingresados:\n\n")
        
        results.append("• Texto Básico: ")
        results.append(if (basicText.isEmpty()) "(vacío)" else "\"$basicText\"")
        results.append("\n\n")
        
        results.append("• Email: ")
        results.append(if (emailText.isEmpty()) "(vacío)" else emailText)
        results.append("\n\n")
        
        results.append("• Contraseña: ")
        results.append(if (passwordText.isEmpty()) "(vacía)" else "${"*".repeat(passwordText.length)} (${passwordText.length} caracteres)")
        results.append("\n\n")
        
        results.append("• Número: ")
        results.append(if (numberText.isEmpty()) "(vacío)" else numberText)

        tvResults.text = results.toString()
        tvResults.visibility = View.VISIBLE
    }

    private fun clearAllFields() {
        etBasic.text.clear()
        etEmail.text.clear()
        etPassword.text.clear()
        etNumber.text.clear()
        tvResults.visibility = View.GONE
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}
