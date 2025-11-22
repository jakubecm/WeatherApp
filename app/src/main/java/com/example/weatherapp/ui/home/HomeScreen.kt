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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.data.HomeLocation
import com.example.weatherapp.ui.components.HourlyForecastCard
import com.example.weatherapp.ui.components.WeatherDetailItem

@Composable
fun HomeScreen(
    homeLocation: HomeLocation,
    modifier: Modifier = Modifier
) {
    // Mock data - later replaced with real data from API
    val mockTemperature = 15
    val mockDescription = "Polojasno"
    val mockFeelsLike = 13
    val mockHumidity = 65
    val mockWindSpeed = 12
    val mockPressure = 1013
    val mockVisibility = 10
    
    val mockHourlyData = listOf(
        Triple("Nyní", "15°", "☀️"),
        Triple("14:00", "16°", "⛅"),
        Triple("15:00", "17°", "⛅"),
        Triple("16:00", "16°", "☁️"),
        Triple("17:00", "15°", "☁️"),
        Triple("18:00", "14°", "🌧️")
    )
    
    Surface(
        modifier = modifier.fillMaxSize(),
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
                text = homeLocation.name,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Aktualizováno právě teď",
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
                        text = "$mockTemperature°C",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Text(
                        text = mockDescription,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Pocitově $mockFeelsLike°C",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Weather icon - placeholder
                Text(
                    text = "⛅",
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
                items(mockHourlyData) { (time, temp, icon) ->
                    HourlyForecastCard(
                        time = time,
                        temperature = temp,
                        icon = icon
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
                    value = "$mockHumidity%",
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
                    value = "$mockWindSpeed km/h",
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
                    value = "$mockPressure hPa",
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
                    value = "$mockVisibility km",
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
