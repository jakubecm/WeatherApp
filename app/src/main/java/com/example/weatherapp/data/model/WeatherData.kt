package com.example.weatherapp.data.model

data class WeatherData(
    val location: LocationInfo,
    val current: CurrentWeather,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>
)

data class LocationInfo(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
)

data class CurrentWeather(
    val temperature: Double,
    val feelsLike: Double,
    val description: String,
    val icon: String,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val windDirection: Int,
    val visibility: Int,
    val uvIndex: Double,
    val clouds: Int,
    val timestamp: Long
)

data class HourlyWeather(
    val timestamp: Long,
    val temperature: Double,
    val description: String,
    val icon: String,
    val pop: Double // Probability of precipitation
)

data class DailyWeather(
    val date: Long,
    val temperatureMin: Double,
    val temperatureMax: Double,
    val description: String,
    val icon: String,
    val pop: Double,
    val humidity: Int,
    val windSpeed: Double
)
