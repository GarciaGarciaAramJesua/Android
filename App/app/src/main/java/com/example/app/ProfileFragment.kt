package com.example.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var etNewItem: EditText
    private lateinit var btnAddItem: Button
    private lateinit var btnAddSample: Button
    private lateinit var btnClearAll: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var listView: ListView
    private lateinit var tvStats: TextView

    private lateinit var profileAdapter: ProfileItemAdapter
    private lateinit var listAdapter: ArrayAdapter<String>
    private val listItems = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        
        initializeViews(view)
        setupRecyclerView()
        setupListView()
        setupInteractions()
        updateStats()
        
        return view
    }

    private fun initializeViews(view: View) {
        etNewItem = view.findViewById(R.id.etNewItem)
        btnAddItem = view.findViewById(R.id.btnAddItem)
        btnAddSample = view.findViewById(R.id.btnAddSample)
        btnClearAll = view.findViewById(R.id.btnClearAll)
        recyclerView = view.findViewById(R.id.recyclerView)
        listView = view.findViewById(R.id.listView)
        tvStats = view.findViewById(R.id.tvStats)
    }

    private fun setupRecyclerView() {
        profileAdapter = ProfileItemAdapter(mutableListOf()) { position ->
            updateStats()
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = profileAdapter
    }

    private fun setupListView() {
        listAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            listItems
        )
        listView.adapter = listAdapter

        // Configurar click para eliminar elementos
        listView.setOnItemClickListener { _, _, position, _ ->
            listItems.removeAt(position)
            listAdapter.notifyDataSetChanged()
            updateStats()
            Toast.makeText(context, "Elemento eliminado del ListView", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupInteractions() {
        btnAddItem.setOnClickListener {
            val text = etNewItem.text.toString().trim()
            if (text.isNotEmpty()) {
                addItemToBothLists(text)
                etNewItem.text.clear()
            } else {
                Toast.makeText(context, "Por favor ingresa un texto", Toast.LENGTH_SHORT).show()
            }
        }

        btnAddSample.setOnClickListener {
            addSampleData()
        }

        btnClearAll.setOnClickListener {
            clearAllLists()
        }

        // Permitir agregar elemento presionando Enter
        etNewItem.setOnEditorActionListener { _, _, _ ->
            btnAddItem.performClick()
            true
        }
    }

    private fun addItemToBothLists(text: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        
        // Agregar a RecyclerView
        val profileItem = ProfileItem(
            title = "📌 $text",
            description = "Elemento agregado por el usuario",
            timestamp = "Agregado a las $timestamp"
        )
        profileAdapter.addItem(profileItem)

        // Agregar a ListView
        listItems.add("🔹 $text (${timestamp})")
        listAdapter.notifyDataSetChanged()

        updateStats()
        Toast.makeText(context, "Elemento agregado a ambas listas", Toast.LENGTH_SHORT).show()
    }

    private fun addSampleData() {
        val sampleItems = listOf(
            "Tarea importante",
            "Recordatorio de reunión", 
            "Nota personal",
            "Lista de compras",
            "Objetivo del día"
        )

        sampleItems.forEach { item ->
            addItemToBothLists(item)
        }

        Toast.makeText(context, "Datos de ejemplo agregados", Toast.LENGTH_SHORT).show()
    }

    private fun clearAllLists() {
        // Limpiar RecyclerView
        profileAdapter.clearAll()

        // Limpiar ListView
        listItems.clear()
        listAdapter.notifyDataSetChanged()

        updateStats()
        Toast.makeText(context, "Todas las listas han sido limpiadas", Toast.LENGTH_SHORT).show()
    }

    private fun updateStats() {
        val recyclerCount = profileAdapter.itemCount
        val listCount = listItems.size
        tvStats.text = "📊 Elementos en RecyclerView: $recyclerCount | ListView: $listCount"
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}
