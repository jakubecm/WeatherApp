package com.example.weatherapp.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OneCallResponse(
    @Json(name = "lat") val lat: Double,
    @Json(name = "lon") val lon: Double,
    @Json(name = "timezone") val timezone: String,
    @Json(name = "current") val current: CurrentWeatherResponse,
    @Json(name = "hourly") val hourly: List<HourlyWeatherResponse>,
    @Json(name = "daily") val daily: List<DailyWeatherResponse>
)

@JsonClass(generateAdapter = true)
data class CurrentWeatherResponse(
    @Json(name = "dt") val dt: Long,
    @Json(name = "temp") val temp: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    @Json(name = "pressure") val pressure: Int,
    @Json(name = "humidity") val humidity: Int,
    @Json(name = "clouds") val clouds: Int,
    @Json(name = "uvi") val uvi: Double,
    @Json(name = "visibility") val visibility: Int,
    @Json(name = "wind_speed") val windSpeed: Double,
    @Json(name = "wind_deg") val windDeg: Int,
    @Json(name = "weather") val weather: List<WeatherCondition>
)

@JsonClass(generateAdapter = true)
data class HourlyWeatherResponse(
    @Json(name = "dt") val dt: Long,
    @Json(name = "temp") val temp: Double,
    @Json(name = "weather") val weather: List<WeatherCondition>,
    @Json(name = "pop") val pop: Double
)

@JsonClass(generateAdapter = true)
data class DailyWeatherResponse(
    @Json(name = "dt") val dt: Long,
    @Json(name = "temp") val temp: TempRange,
    @Json(name = "weather") val weather: List<WeatherCondition>,
    @Json(name = "pop") val pop: Double,
    @Json(name = "humidity") val humidity: Int,
    @Json(name = "wind_speed") val windSpeed: Double
)

@JsonClass(generateAdapter = true)
data class TempRange(
    @Json(name = "min") val min: Double,
    @Json(name = "max") val max: Double
)

@JsonClass(generateAdapter = true)
data class WeatherCondition(
    @Json(name = "id") val id: Int,
    @Json(name = "main") val main: String,
    @Json(name = "description") val description: String,
    @Json(name = "icon") val icon: String
)
