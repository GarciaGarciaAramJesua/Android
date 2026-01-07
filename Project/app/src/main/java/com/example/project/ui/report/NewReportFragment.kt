package com.example.project.ui.report

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.project.R
import com.example.project.data.model.Report
import com.example.project.data.model.ReportType
import com.example.project.databinding.FragmentNewReportBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputEditText
import android.widget.LinearLayout
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date
import java.util.Locale

class NewReportFragment : Fragment() {

    private var _binding: FragmentNewReportBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ReportViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
    private var currentLocation: GeoPoint? = null
    private var currentLocationAddress: String = ""
    private var selectedPhotoUri: Uri? = null
    private var currentPhotoPath: String? = null
    
    private val dynamicFields = mutableMapOf<String, TextInputEditText>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            getCurrentLocation()
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoPath?.let { path ->
                selectedPhotoUri = Uri.fromFile(File(path))
                binding.photoImageView.setImageURI(selectedPhotoUri)
                binding.photoImageView.visibility = View.VISIBLE
            }
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedPhotoUri = result.data?.data
            binding.photoImageView.setImageURI(selectedPhotoUri)
            binding.photoImageView.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        
        setupReportTypeDropdown()
        setupLocationButton()
        setupPhotoButtons()
        setupSubmitButton()
        observeViewModel()
        
        requestLocationPermission()
    }

    private fun setupReportTypeDropdown() {
        val reportTypes = ReportType.values().map { it.getDisplayName() }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, reportTypes)
        binding.typeDropdown.setAdapter(adapter)
        
        binding.typeDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedType = ReportType.values()[position]
            updateDynamicFields(selectedType)
        }
    }

    private fun updateDynamicFields(reportType: ReportType) {
        binding.dynamicFieldsContainer.removeAllViews()
        dynamicFields.clear()
        
        when (reportType) {
            ReportType.SERVICIOS_PUBLICOS -> {
                addDynamicField("serviceType", "Tipo de Servicio", "ej: Baches, Luminarias, Fugas")
            }
            ReportType.ROBO_ASALTO -> {
                addDynamicField("stolenObjects", "Objetos Sustraídos", "Descripción de objetos robados")
            }
            ReportType.CORRUPCION -> {
                addDynamicField("dependencyName", "Dependencia", "Nombre de la dependencia")
                addDynamicField("officialName", "Servidor Público (opcional)", "Nombre del servidor público")
            }
            ReportType.VIOLENCIA_GENERO -> {
                addDynamicField("violenceType", "Tipo de Violencia", "ej: Física, Psicológica, Sexual")
                addDynamicField("aggressorRelation", "Relación con Agresor", "ej: Pareja, Familiar, Desconocido")
            }
            ReportType.NARCOMENUDEO -> {
                addDynamicField("suspiciousActivity", "Actividad Sospechosa", "Describa la actividad")
                addDynamicField("personDescription", "Descripción de Personas", "Características físicas")
                addDynamicField("vehicleDescription", "Descripción de Vehículos", "Marca, color, placas")
            }
            ReportType.REPORTE_GENERAL -> {
                // No se requieren campos adicionales
            }
        }
    }

    private fun addDynamicField(key: String, hint: String, helperText: String) {
        val layout = TextInputLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16
            }
            setHint(hint)
            setHelperText(helperText)
        }
        
        val editText = TextInputEditText(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        
        layout.addView(editText)
        binding.dynamicFieldsContainer.addView(layout)
        dynamicFields[key] = editText
    }

    private fun setupLocationButton() {
        binding.refreshLocationButton.setOnClickListener {
            getCurrentLocation()
        }
    }

    private fun setupPhotoButtons() {
        binding.cameraButton.setOnClickListener {
            val photoFile = createImageFile()
            currentPhotoPath = photoFile.absolutePath
            val photoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile
            )
            takePictureLauncher.launch(photoUri)
        }

        binding.galleryButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }
    }

    private fun setupSubmitButton() {
        binding.submitButton.setOnClickListener {
            submitReport()
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentLocation = GeoPoint(it.latitude, it.longitude)
                    getAddressFromLocation(it.latitude, it.longitude)
                }
            }
        }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        lifecycleScope.launch {
            try {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    currentLocationAddress = addresses[0].getAddressLine(0) ?: ""
                    binding.locationTextView.text = currentLocationAddress
                } else {
                    binding.locationTextView.text = "Lat: $latitude, Lng: $longitude"
                }
            } catch (e: Exception) {
                binding.locationTextView.text = "Lat: $latitude, Lng: $longitude"
            }
        }
    }

    private fun submitReport() {
        val typePosition = ReportType.values().indexOfFirst { 
            it.getDisplayName() == binding.typeDropdown.text.toString() 
        }
        
        if (typePosition == -1) {
            Toast.makeText(requireContext(), "Selecciona un tipo de reporte", Toast.LENGTH_SHORT).show()
            return
        }
        
        val reportType = ReportType.values()[typePosition]
        
        val report = Report(
            type = reportType.name,
            alias = binding.aliasEditText.text.toString().ifEmpty { "Anónimo" },
            description = binding.descriptionEditText.text.toString(),
            location = currentLocation,
            locationAddress = currentLocationAddress,
            timestamp = Date(),
            serviceType = dynamicFields["serviceType"]?.text.toString(),
            stolenObjects = dynamicFields["stolenObjects"]?.text.toString(),
            dependencyName = dynamicFields["dependencyName"]?.text.toString(),
            officialName = dynamicFields["officialName"]?.text.toString(),
            violenceType = dynamicFields["violenceType"]?.text.toString(),
            aggressorRelation = dynamicFields["aggressorRelation"]?.text.toString(),
            suspiciousActivity = dynamicFields["suspiciousActivity"]?.text.toString(),
            personDescription = dynamicFields["personDescription"]?.text.toString(),
            vehicleDescription = dynamicFields["vehicleDescription"]?.text.toString()
        )
        
        viewModel.createReport(report, selectedPhotoUri)
    }

    private fun observeViewModel() {
        viewModel.reportCreationStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is ReportCreationStatus.Loading -> {
                    binding.submitButton.isEnabled = false
                    binding.submitButton.text = "Enviando..."
                }
                is ReportCreationStatus.Success -> {
                    Toast.makeText(requireContext(), "Reporte enviado exitosamente", Toast.LENGTH_LONG).show()
                    clearForm()
                    binding.submitButton.isEnabled = true
                    binding.submitButton.text = "Enviar Reporte"
                }
                is ReportCreationStatus.Error -> {
                    Toast.makeText(requireContext(), "Error: ${status.message}", Toast.LENGTH_LONG).show()
                    binding.submitButton.isEnabled = true
                    binding.submitButton.text = "Enviar Reporte"
                }
            }
        }
    }

    private fun clearForm() {
        binding.typeDropdown.text.clear()
        binding.aliasEditText.text?.clear()
        binding.descriptionEditText.text?.clear()
        binding.dynamicFieldsContainer.removeAllViews()
        dynamicFields.clear()
        binding.photoImageView.visibility = View.GONE
        selectedPhotoUri = null
    }

    private fun createImageFile(): File {
        val timeStamp = System.currentTimeMillis()
        val storageDir = requireContext().getExternalFilesDir(null)
        return File(storageDir, "REPORT_${timeStamp}.jpg")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
