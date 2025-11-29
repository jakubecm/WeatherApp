package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.data.HomeLocation
import com.example.weatherapp.data.PreferencesManager
import com.example.weatherapp.ui.WeatherPagerScreen
import com.example.weatherapp.ui.home.HomeViewModel
import com.example.weatherapp.ui.setup.LocationSetupScreen
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val preferencesManager = PreferencesManager(this)
        
        setContent {
            WeatherAppTheme {
                val viewModel: HomeViewModel = viewModel()
                var homeLocation by remember { mutableStateOf<HomeLocation?>(null) }
                var isLoading by remember { mutableStateOf(true) }
                val coroutineScope = rememberCoroutineScope()
                
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
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}