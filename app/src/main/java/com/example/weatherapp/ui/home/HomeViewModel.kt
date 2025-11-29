package com.example.weatherapp.ui.home

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.HomeLocation
import com.example.weatherapp.data.model.WeatherData
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.location.LocationHelper
import com.example.weatherapp.location.LocationSearchResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {
    
    private val repository: WeatherRepository = WeatherRepository()
    private val locationHelper: LocationHelper = LocationHelper(application)
    private var searchJob: Job? = null
    
    var weatherData by mutableStateOf<WeatherData?>(null)
        private set
    
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var isRefreshing by mutableStateOf(false)
        private set
    
    var searchQuery by mutableStateOf("")
        private set
    
    var searchResults by mutableStateOf<List<LocationSearchResult>>(emptyList())
        private set
    
    var isSearching by mutableStateOf(false)
        private set
    
    fun updateSearchQuery(query: String) {
        searchQuery = query
        searchJob?.cancel()
        
        if (query.isBlank()) {
            searchResults = emptyList()
            return
        }
        
        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            isSearching = true
            searchResults = locationHelper.searchLocations(query)
            isSearching = false
        }
    }
    
    fun clearSearch() {
        searchQuery = ""
        searchResults = emptyList()
    }
    
    fun loadWeather(homeLocation: HomeLocation) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            
            val latitude = homeLocation.latitude.toDoubleOrNull()
            val longitude = homeLocation.longitude.toDoubleOrNull()
            
            if (latitude == null || longitude == null) {
                errorMessage = "Neplatné souřadnice"
                isLoading = false
                return@launch
            }
            
            repository.getWeather(
                latitude = latitude,
                longitude = longitude,
                locationName = homeLocation.name
            ).onSuccess { data ->
                weatherData = data
                errorMessage = null
            }.onFailure { error ->
                errorMessage = "Chyba při načítání počasí: ${error.message}"
            }
            
            isLoading = false
        }
    }
    
    fun refreshWeather(homeLocation: HomeLocation) {
        viewModelScope.launch {
            isRefreshing = true
            errorMessage = null
            
            val latitude = homeLocation.latitude.toDoubleOrNull()
            val longitude = homeLocation.longitude.toDoubleOrNull()
            
            if (latitude == null || longitude == null) {
                errorMessage = "Neplatné souřadnice"
                isRefreshing = false
                return@launch
            }
            
            repository.getWeather(
                latitude = latitude,
                longitude = longitude,
                locationName = homeLocation.name
            ).onSuccess { data ->
                weatherData = data
                errorMessage = null
            }.onFailure { error ->
                errorMessage = "Chyba při obnovení: ${error.message}"
            }
            
            isRefreshing = false
        }
    }
}
