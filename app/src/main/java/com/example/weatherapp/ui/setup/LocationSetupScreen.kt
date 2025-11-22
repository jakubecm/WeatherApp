package com.example.weatherapp.ui.setup

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.data.HomeLocation
import com.example.weatherapp.data.PreferencesManager
import com.example.weatherapp.location.LocationHelper
import com.example.weatherapp.location.LocationResult
import kotlinx.coroutines.launch

class LocationSetupViewModel(
    private val preferencesManager: PreferencesManager,
    private val locationHelper: LocationHelper
) : ViewModel() {
    
    var locationName by mutableStateOf("")
        private set
    
    var latitude by mutableStateOf("")
        private set
    
    var longitude by mutableStateOf("")
        private set
    
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        internal set
    
    var showSuccess by mutableStateOf(false)
        private set
    
    fun updateLocationName(name: String) {
        locationName = name
        errorMessage = null
    }
    
    fun updateLatitude(lat: String) {
        latitude = lat
        errorMessage = null
    }
    
    fun updateLongitude(lon: String) {
        longitude = lon
        errorMessage = null
    }
    
    fun getCurrentLocation() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            
            when (val result = locationHelper.getCurrentLocation()) {
                is LocationResult.Success -> {
                    locationName = result.cityName
                    latitude = result.latitude.toString()
                    longitude = result.longitude.toString()
                }
                is LocationResult.Error -> {
                    errorMessage = result.message
                }
            }
            
            isLoading = false
        }
    }
    
    fun saveLocation(onSuccess: () -> Unit) {
        if (locationName.isBlank()) {
            errorMessage = "Zadejte název místa"
            return
        }
        
        if (latitude.isBlank() || longitude.isBlank()) {
            errorMessage = "Zadejte souřadnice nebo použijte geolokaci"
            return
        }
        
        viewModelScope.launch {
            isLoading = true
            try {
                preferencesManager.saveHomeLocation(
                    HomeLocation(
                        name = locationName,
                        latitude = latitude,
                        longitude = longitude
                    )
                )
                showSuccess = true
                isLoading = false
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "Chyba při ukládání: ${e.message}"
                isLoading = false
            }
        }
    }
}

@Composable
fun LocationSetupScreen(
    onLocationSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val locationHelper = remember { LocationHelper(context) }
    val viewModel: LocationSetupViewModel = remember {
        LocationSetupViewModel(preferencesManager, locationHelper)
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.getCurrentLocation()
        } else {
            viewModel.errorMessage = "Povolení k poloze bylo zamítnuto"
        }
    }
    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Vítejte v Weather App",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Nastavte si své domovské místo",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            OutlinedTextField(
                value = viewModel.locationName,
                onValueChange = { viewModel.updateLocationName(it) },
                label = { Text("Název místa") },
                placeholder = { Text("např. Praha") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.latitude,
                    onValueChange = { viewModel.updateLatitude(it) },
                    label = { Text("Zeměpisná šířka") },
                    placeholder = { Text("50.0755") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = viewModel.longitude,
                    onValueChange = { viewModel.updateLongitude(it) },
                    label = { Text("Zeměpisná délka") },
                    placeholder = { Text("14.4378") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    if (locationHelper.hasLocationPermission()) {
                        viewModel.getCurrentLocation()
                    } else {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Použít aktuální polohu")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { viewModel.saveLocation(onLocationSaved) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !viewModel.isLoading
            ) {
                Text("Uložit místo")
            }
            
            if (viewModel.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
            
            viewModel.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
