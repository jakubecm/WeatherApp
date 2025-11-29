package com.example.weatherapp

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.HomeLocation
import com.example.weatherapp.data.PreferencesManager
import com.example.weatherapp.location.LocationHelper
import com.example.weatherapp.location.LocationResult
import com.example.weatherapp.ui.WeatherPagerScreen
import com.example.weatherapp.ui.home.HomeViewModel
import com.example.weatherapp.ui.settings.SettingsScreen
import com.example.weatherapp.ui.setup.LocationSetupScreen
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val preferencesManager = PreferencesManager(this)
        val locationHelper = LocationHelper(this)
        
        setContent {
            val darkMode by preferencesManager.darkMode.collectAsState(initial = false)
            
            WeatherAppTheme(darkTheme = darkMode) {
                val context = LocalContext.current
                val viewModel: HomeViewModel = viewModel(
                    factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                )
                var homeLocation by remember { mutableStateOf<HomeLocation?>(null) }
                var isLoading by remember { mutableStateOf(true) }
                var showSettings by remember { mutableStateOf(false) }
                val coroutineScope = rememberCoroutineScope()
                
                val locationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        coroutineScope.launch {
                            when (val result = locationHelper.getCurrentLocation()) {
                                is LocationResult.Success -> {
                                    val newLocation = HomeLocation(
                                        name = result.cityName,
                                        latitude = result.latitude.toString(),
                                        longitude = result.longitude.toString()
                                    )
                                    preferencesManager.saveHomeLocation(newLocation)
                                    Toast.makeText(context, "Poloha nalezena: ${result.cityName}", Toast.LENGTH_SHORT).show()
                                }
                                is LocationResult.Error -> {
                                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Oprávnění k poloze bylo zamítnuto", Toast.LENGTH_SHORT).show()
                    }
                }
                
                LaunchedEffect(Unit) {
                    preferencesManager.homeLocation.collect { location ->
                        homeLocation = location
                        isLoading = false
        
                        location?.let {
                            viewModel.loadWeather(it)
                        }
                    }
                }
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (showSettings && homeLocation != null) {
                        SettingsScreen(
                            darkMode = darkMode,
                            onDarkModeChange = { enabled ->
                                coroutineScope.launch {
                                    preferencesManager.setDarkMode(enabled)
                                }
                            },
                            homeLocation = homeLocation,
                            onChangeLocationClick = {
                                showSettings = false
                            },
                            onBackClick = { showSettings = false },
                            modifier = Modifier.padding(innerPadding),
                            onSearchQuery = { query ->
                                viewModel.updateSearchQuery(query)
                            },
                            searchResults = viewModel.searchResults,
                            isSearching = viewModel.isSearching,
                            onLocationSelected = { result ->
                                coroutineScope.launch {
                                    val newLocation = HomeLocation(
                                        name = result.cityName,
                                        latitude = result.latitude.toString(),
                                        longitude = result.longitude.toString()
                                    )
                                    preferencesManager.saveHomeLocation(newLocation)
                                    viewModel.clearSearch()
                                    Toast.makeText(context, "Místo změněno: ${result.cityName}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    } else {
                        when {
                            isLoading -> {
                                // Prázdná obrazovka při načítání
                            }
                            homeLocation == null -> {
                                // Pokud není uložené místo, zobrazit setup
                                LocationSetupScreen(
                                    onLocationSaved = {
                                        // Po uložení místa znovu načíst

                                    },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                            else -> {
                                // Pokud je uložené místo, zobrazit pager s oběma obrazovkami
                                WeatherPagerScreen(
                                    viewModel = viewModel,
                                    homeLocation = homeLocation!!,
                                    onRefreshRequested = {
                                        coroutineScope.launch {
                                            homeLocation?.let {
                                                viewModel.refreshWeather(it)
                                            }
                                        }
                                    },
                                    onLocationSelected = { lat, lon, name ->
                                        coroutineScope.launch {
                                            val newLocation = HomeLocation(
                                                name = name,
                                                latitude = lat.toString(),
                                                longitude = lon.toString()
                                            )
                                            preferencesManager.saveHomeLocation(newLocation)
                                        }
                                    },
                                    onCurrentLocationClick = {
                                        if (locationHelper.hasLocationPermission()) {
                                            coroutineScope.launch {
                                                Toast.makeText(context, "Získávám polohu...", Toast.LENGTH_SHORT).show()
                                                when (val result = locationHelper.getCurrentLocation()) {
                                                    is LocationResult.Success -> {
                                                        val newLocation = HomeLocation(
                                                            name = result.cityName,
                                                            latitude = result.latitude.toString(),
                                                            longitude = result.longitude.toString()
                                                        )
                                                        preferencesManager.saveHomeLocation(newLocation)
                                                        Toast.makeText(context, "Poloha nalezena: ${result.cityName}", Toast.LENGTH_SHORT).show()
                                                    }
                                                    is LocationResult.Error -> {
                                                        Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                            }
                                        } else {
                                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                        }
                                    },
                                    onSettingsClick = { showSettings = true }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}