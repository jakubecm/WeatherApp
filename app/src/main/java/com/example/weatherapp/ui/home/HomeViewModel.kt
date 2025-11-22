package com.example.weatherapp.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.HomeLocation
import com.example.weatherapp.data.model.WeatherData
import com.example.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: WeatherRepository = WeatherRepository()
) : ViewModel() {
    
    var weatherData by mutableStateOf<WeatherData?>(null)
        private set
    
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var isRefreshing by mutableStateOf(false)
        private set
    
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
