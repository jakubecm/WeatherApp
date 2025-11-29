package com.example.weatherapp.ui.forecast

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.ui.components.DailyForecastCard
import com.example.weatherapp.ui.home.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun ForecastScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val weatherData = viewModel.weatherData
    
    if (weatherData == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Nadpis
            Text(
                text = "Týdenní předpověď",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = weatherData.location.name,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Denní předpověď - pouze 7 dní
            weatherData.daily.take(7).forEach { day ->
                val dateFormat = SimpleDateFormat("EEEE", Locale.forLanguageTag("cs"))
                val dayName = dateFormat.format(Date(day.date * 1000))
                
                DailyForecastCard(
                    dayName = dayName.replaceFirstChar { it.uppercase() },
                    icon = getWeatherEmoji(day.icon),
                    maxTemp = day.temperatureMax.roundToInt(),
                    minTemp = day.temperatureMin.roundToInt(),
                    description = day.description,
                    pop = (day.pop * 100).roundToInt()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun getWeatherEmoji(icon: String): String {
    return when {
        icon.startsWith("01") -> "☀️"
        icon.startsWith("02") -> "⛅"
        icon.startsWith("03") -> "☁️"
        icon.startsWith("04") -> "☁️"
        icon.startsWith("09") -> "🌧️"
        icon.startsWith("10") -> "🌦️"
        icon.startsWith("11") -> "⛈️"
        icon.startsWith("13") -> "❄️"
        icon.startsWith("50") -> "🌫️"
        else -> "🌤️"
    }
}
