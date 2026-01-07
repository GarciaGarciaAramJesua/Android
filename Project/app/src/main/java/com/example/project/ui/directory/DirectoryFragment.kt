package com.example.project.ui.directory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.R
import com.example.project.data.model.Institution
import com.example.project.databinding.FragmentDirectoryBinding

class DirectoryFragment : Fragment() {

    private var _binding: FragmentDirectoryBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DirectoryViewModel by viewModels()
    private lateinit var adapter: InstitutionAdapter
    
    private val requestCallPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(requireContext(), "Permiso de llamada denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDirectoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSearchBar()
        setupCategoryFilters()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = InstitutionAdapter(
            onCallClick = { institution -> makePhoneCall(institution) },
            onLocationClick = { institution -> openLocation(institution) },
            onWebsiteClick = { institution -> openWebsite(institution) }
        )
        
        binding.institutionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@DirectoryFragment.adapter
        }
    }

    private fun setupSearchBar() {
        binding.searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchInstitutions(s.toString())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun setupCategoryFilters() {
        binding.chipAll.setOnClickListener {
            viewModel.filterByCategory("all")
        }
        
        binding.chipSeguridad.setOnClickListener {
            viewModel.filterByCategory("seguridad")
        }
        
        binding.chipAgua.setOnClickListener {
            viewModel.filterByCategory("agua")
        }
        
        binding.chipGenero.setOnClickListener {
            viewModel.filterByCategory("género")
        }
        
        binding.chipSalud.setOnClickListener {
            viewModel.filterByCategory("salud")
        }
        
        binding.chipEducacion.setOnClickListener {
            viewModel.filterByCategory("educación")
        }
        
        binding.chipGeneral.setOnClickListener {
            viewModel.filterByCategory("general")
        }
    }

    private fun observeViewModel() {
        viewModel.filteredInstitutions.observe(viewLifecycleOwner) { institutions ->
            adapter.submitList(institutions)
        }
    }

    private fun makePhoneCall(institution: Institution) {
        if (institution.phone.isEmpty()) {
            Toast.makeText(requireContext(), "No hay teléfono disponible", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:${institution.phone}")
            }
            startActivity(intent)
        } else {
            requestCallPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }
    }

    private fun openLocation(institution: Institution) {
        if (institution.latitude != 0.0 && institution.longitude != 0.0) {
            val gmmIntentUri = Uri.parse("geo:${institution.latitude},${institution.longitude}?q=${institution.latitude},${institution.longitude}(${institution.name})")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            
            if (mapIntent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(mapIntent)
            } else {
                // Si Google Maps no está instalado, abrir en navegador
                val browserIntent = Intent(Intent.ACTION_VIEW, 
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=${institution.latitude},${institution.longitude}"))
                startActivity(browserIntent)
            }
        } else {
            Toast.makeText(requireContext(), "Ubicación no disponible", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openWebsite(institution: Institution) {
        if (institution.website.isEmpty()) {
            Toast.makeText(requireContext(), "Sitio web no disponible", Toast.LENGTH_SHORT).show()
            return
        }
        
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(institution.website))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
