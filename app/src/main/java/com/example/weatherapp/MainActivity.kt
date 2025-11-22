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
import com.example.weatherapp.data.HomeLocation
import com.example.weatherapp.data.PreferencesManager
import com.example.weatherapp.ui.home.HomeScreen
import com.example.weatherapp.ui.setup.LocationSetupScreen
import com.example.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val preferencesManager = PreferencesManager(this)
        
        setContent {
            WeatherAppTheme {
                var homeLocation by remember { mutableStateOf<HomeLocation?>(null) }
                var isLoading by remember { mutableStateOf(true) }
                
                // Načtení uloženého místa při spuštění a sledování změn
                LaunchedEffect(Unit) {
                    preferencesManager.homeLocation.collect { location ->
                        homeLocation = location
                        isLoading = false
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
                                    // Toto bude fungovat protože už jsme v Compose scope
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        else -> {
                            // Pokud je uložené místo, zobrazit hlavní obrazovku
                            HomeScreen(
                                homeLocation = homeLocation!!,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}