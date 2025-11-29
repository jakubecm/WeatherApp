package com.example.weatherapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun DailyForecastCard(
    dayName: String,
    icon: String,
    maxTemp: Int,
    minTemp: Int,
    description: String,
    pop: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Den
            Text(
                text = dayName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(0.25f)
            )
            
            // Ikona + popis
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(0.35f)
            ) {
                Text(
                    text = icon,
                    fontSize = 32.sp
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Pravděpodobnost srážek
            if (pop > 0) {
                Text(
                    text = "$pop%",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(0.15f)
                )
            } else {
                Spacer(modifier = Modifier.weight(0.15f))
            }
            
            // Teploty
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(0.25f)
            ) {
                Text(
                    text = "$maxTemp°",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = " / ",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$minTemp°",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DetailInfoCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                icon()
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            subtitle?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
