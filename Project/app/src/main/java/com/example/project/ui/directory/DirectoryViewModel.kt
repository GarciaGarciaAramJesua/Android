package com.example.project.ui.directory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.data.model.Institution
import com.example.project.data.repository.InstitutionRepository
import kotlinx.coroutines.launch

class DirectoryViewModel : ViewModel() {
    
    private val repository = InstitutionRepository()
    
    private val _institutions = MutableLiveData<List<Institution>>()
    val institutions: LiveData<List<Institution>> = _institutions
    
    private val _filteredInstitutions = MutableLiveData<List<Institution>>()
    val filteredInstitutions: LiveData<List<Institution>> = _filteredInstitutions
    
    private val _selectedCategory = MutableLiveData<String>("all")
    val selectedCategory: LiveData<String> = _selectedCategory
    
    init {
        loadInstitutions()
    }
    
    private fun loadInstitutions() {
        viewModelScope.launch {
            repository.initializeInstitutions()
            repository.getAllInstitutions().collect { institutionsList ->
                _institutions.value = institutionsList
                applyFilter()
            }
        }
    }
    
    fun filterByCategory(category: String) {
        _selectedCategory.value = category
        applyFilter()
    }
    
    private fun applyFilter() {
        val category = _selectedCategory.value ?: "all"
        val allInstitutions = _institutions.value ?: emptyList()
        
        _filteredInstitutions.value = if (category == "all") {
            allInstitutions
        } else {
            allInstitutions.filter { it.category == category }
        }
    }
    
    fun searchInstitutions(query: String) {
        val allInstitutions = _institutions.value ?: emptyList()
        val category = _selectedCategory.value ?: "all"
        
        val filtered = allInstitutions.filter { institution ->
            val matchesSearch = institution.name.contains(query, ignoreCase = true) ||
                    institution.description.contains(query, ignoreCase = true)
            val matchesCategory = category == "all" || institution.category == category
            
            matchesSearch && matchesCategory
        }
        
        _filteredInstitutions.value = filtered
    }
}
