package com.example.weatherapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.data.HomeLocation
import com.example.weatherapp.ui.components.HourlyForecastCard
import com.example.weatherapp.ui.components.WeatherDetailItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    homeLocation: HomeLocation,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val weatherData = viewModel.weatherData
    val isLoading = viewModel.isLoading
    val isRefreshing = viewModel.isRefreshing
    val errorMessage = viewModel.errorMessage
    
    // Načtení dat při prvním zobrazení
    LaunchedEffect(homeLocation) {
        if (weatherData == null && !isLoading) {
            viewModel.loadWeather(homeLocation)
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        when {
            isLoading && weatherData == null -> {
                // Zobrazení načítání při prvním načtení
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null && weatherData == null -> {
                // Zobrazení chyby když nejsou data
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadWeather(homeLocation) }) {
                            Text("Zkusit znovu")
                        }
                    }
                }
            }
            weatherData != null -> {
                WeatherContent(
                    weatherData = weatherData,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        
        // FAB pro refresh
        FloatingActionButton(
            onClick = { viewModel.refreshWeather(homeLocation) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            if (isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Obnovit počasí"
                )
            }
        }
    }
}

@Composable
private fun WeatherContent(
    weatherData: com.example.weatherapp.data.model.WeatherData,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header - Location
            Text(
                text = weatherData.location.name,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            val updateTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(Date(weatherData.current.timestamp * 1000))
            Text(
                text = "Aktualizováno v $updateTime",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Current temperature and icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${weatherData.current.temperature.roundToInt()}°C",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Text(
                        text = weatherData.current.description,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Pocitově ${weatherData.current.feelsLike.roundToInt()}°C",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Weather icon - placeholder (můžeme později nahradit skutečnou ikonou)
                Text(
                    text = getWeatherEmoji(weatherData.current.icon),
                    fontSize = 80.sp
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Hourly Forecast
            Text(
                text = "Hodinová předpověď",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(weatherData.hourly.take(12)) { hourly ->
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val time = timeFormat.format(Date(hourly.timestamp * 1000))
                    
                    HourlyForecastCard(
                        time = time,
                        temperature = "${hourly.temperature.roundToInt()}°",
                        icon = getWeatherEmoji(hourly.icon)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Details
            Text(
                text = "Detaily",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WeatherDetailItem(
                    label = "Vlhkost",
                    value = "${weatherData.current.humidity}%",
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.WaterDrop,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
                
                WeatherDetailItem(
                    label = "Vítr",
                    value = "${weatherData.current.windSpeed.roundToInt()} km/h",
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Air,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WeatherDetailItem(
                    label = "Tlak",
                    value = "${weatherData.current.pressure} hPa",
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Compress,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
                
                WeatherDetailItem(
                    label = "Viditelnost",
                    value = "${weatherData.current.visibility} km",
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun getWeatherEmoji(icon: String): String {
    return when {
        icon.startsWith("01") -> "☀️" // clear sky
        icon.startsWith("02") -> "⛅" // few clouds
        icon.startsWith("03") -> "☁️" // scattered clouds
        icon.startsWith("04") -> "☁️" // broken clouds
        icon.startsWith("09") -> "🌧️" // shower rain
        icon.startsWith("10") -> "🌦️" // rain
        icon.startsWith("11") -> "⛈️" // thunderstorm
        icon.startsWith("13") -> "❄️" // snow
        icon.startsWith("50") -> "🌫️" // mist
        else -> "🌤️"
    }
}
