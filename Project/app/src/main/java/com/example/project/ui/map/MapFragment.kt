package com.example.project.ui.map

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.project.R
import com.example.project.data.model.Report
import com.example.project.data.model.ReportType
import com.example.project.data.model.SeverityLevel
import com.example.project.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.text.SimpleDateFormat
import java.util.Locale

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MapViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private val markers = mutableMapOf<Marker, Report>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        
        setupDetailCard()
        observeViewModel()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        // Centrar en Ciudad de México
        val cdmx = LatLng(19.4326, -99.1332)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(cdmx, 11f))
        
        googleMap?.setOnMarkerClickListener { marker ->
            markers[marker]?.let { report ->
                viewModel.selectReport(report)
            }
            true
        }
        
        googleMap?.setOnMapClickListener {
            viewModel.clearSelectedReport()
        }
    }

    private fun setupDetailCard() {
        binding.closeButton.setOnClickListener {
            viewModel.clearSelectedReport()
        }
    }

    private fun observeViewModel() {
        viewModel.reports.observe(viewLifecycleOwner) { reports ->
            updateMapMarkers(reports)
            updateHeatmap(reports)
        }
        
        viewModel.selectedReport.observe(viewLifecycleOwner) { report ->
            if (report != null) {
                showReportDetail(report)
            } else {
                hideReportDetail()
            }
        }
    }

    private fun updateMapMarkers(reports: List<Report>) {
        googleMap?.clear()
        markers.clear()
        
        val boundsBuilder = LatLngBounds.Builder()
        var hasMarkers = false
        
        reports.forEach { report ->
            report.location?.let { location ->
                val position = LatLng(location.latitude, location.longitude)
                
                val markerColor = when (ReportType.valueOf(report.type)) {
                    ReportType.SERVICIOS_PUBLICOS -> BitmapDescriptorFactory.HUE_BLUE
                    ReportType.ROBO_ASALTO -> BitmapDescriptorFactory.HUE_RED
                    ReportType.CORRUPCION -> BitmapDescriptorFactory.HUE_ORANGE
                    ReportType.VIOLENCIA_GENERO -> BitmapDescriptorFactory.HUE_VIOLET
                    ReportType.NARCOMENUDEO -> BitmapDescriptorFactory.HUE_YELLOW
                    ReportType.REPORTE_GENERAL -> BitmapDescriptorFactory.HUE_GREEN
                }
                
                val marker = googleMap?.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(ReportType.valueOf(report.type).getDisplayName())
                        .snippet(report.description.take(50))
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                )
                
                marker?.let {
                    markers[it] = report
                    boundsBuilder.include(position)
                    hasMarkers = true
                }
            }
        }
        
        if (hasMarkers) {
            try {
                val bounds = boundsBuilder.build()
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            } catch (e: Exception) {
                // Si solo hay un marcador, centrar en él
            }
        }
    }

    private fun updateHeatmap(reports: List<Report>) {
        // Agrupar reportes por zona aproximada (redondear coordenadas)
        val zoneReports = mutableMapOf<String, Int>()
        
        reports.forEach { report ->
            report.location?.let { location ->
                val zoneKey = "${(location.latitude * 100).toInt()}_${(location.longitude * 100).toInt()}"
                zoneReports[zoneKey] = (zoneReports[zoneKey] ?: 0) + 1
            }
        }
        
        // Dibujar círculos coloreados por densidad
        zoneReports.forEach { (zoneKey, count) ->
            val parts = zoneKey.split("_")
            val lat = parts[0].toDouble() / 100
            val lng = parts[1].toDouble() / 100
            
            val severity = SeverityLevel.fromCount(count)
            val color = when (severity) {
                SeverityLevel.LOW -> Color.argb(50, 0, 255, 0)
                SeverityLevel.MEDIUM -> Color.argb(50, 255, 165, 0)
                SeverityLevel.HIGH -> Color.argb(50, 255, 0, 0)
            }
            
            googleMap?.addCircle(
                CircleOptions()
                    .center(LatLng(lat, lng))
                    .radius(500.0) // 500 metros
                    .fillColor(color)
                    .strokeColor(Color.TRANSPARENT)
            )
        }
    }

    private fun showReportDetail(report: Report) {
        binding.reportDetailCard.visibility = View.VISIBLE
        
        val reportType = try {
            ReportType.valueOf(report.type)
        } catch (e: Exception) {
            ReportType.REPORTE_GENERAL
        }
        
        binding.reportTypeTextView.text = reportType.getDisplayName()
        binding.reportAliasTextView.text = "Reportado por: ${report.alias}"
        
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        binding.reportDateTextView.text = dateFormat.format(report.timestamp)
        
        binding.reportDescriptionTextView.text = report.description
        binding.reportLocationTextView.text = report.locationAddress
        
        if (report.photoUrl.isNotEmpty()) {
            binding.reportPhotoImageView.visibility = View.VISIBLE
            Glide.with(this)
                .load(report.photoUrl)
                .into(binding.reportPhotoImageView)
        } else {
            binding.reportPhotoImageView.visibility = View.GONE
        }
        
        // Animar la cámara hacia el reporte seleccionado
        report.location?.let { location ->
            val position = LatLng(location.latitude, location.longitude)
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
        }
    }

    private fun hideReportDetail() {
        binding.reportDetailCard.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
