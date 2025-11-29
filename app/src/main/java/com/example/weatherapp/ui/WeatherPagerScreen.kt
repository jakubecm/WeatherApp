package com.example.weatherapp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.weatherapp.data.HomeLocation
import com.example.weatherapp.ui.forecast.ForecastScreen
import com.example.weatherapp.ui.home.HomeScreen
import com.example.weatherapp.ui.home.HomeViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeatherPagerScreen(
    viewModel: HomeViewModel,
    homeLocation: HomeLocation,
    onRefreshRequested: () -> Unit,
    onLocationSelected: (Double, Double, String) -> Unit,
    onCurrentLocationClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 2 })

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> HomeScreen(
                    viewModel = viewModel,
                    homeLocation = homeLocation,
                    onRefreshRequested = onRefreshRequested,
                    onLocationSelected = onLocationSelected,
                    onCurrentLocationClick = onCurrentLocationClick,
                    onSettingsClick = onSettingsClick
                )
                1 -> ForecastScreen(
                    viewModel = viewModel
                )
            }
        }

        // Page indicator
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(2) { index ->
                val color = if (pagerState.currentPage == index) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                }
                
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .background(color, shape = androidx.compose.foundation.shape.CircleShape)
                )
            }
        }
    }
}
