package com.example.weatherapp.data.mapper

import com.example.weatherapp.data.api.OneCallResponse
import com.example.weatherapp.data.model.*

fun OneCallResponse.toWeatherData(locationName: String, country: String = "CZ"): WeatherData {
    return WeatherData(
        location = LocationInfo(
            name = locationName,
            country = country,
            latitude = lat,
            longitude = lon
        ),
        current = CurrentWeather(
            temperature = current.temp,
            feelsLike = current.feelsLike,
            description = current.weather.firstOrNull()?.description?.capitalize() ?: "",
            icon = current.weather.firstOrNull()?.icon ?: "",
            humidity = current.humidity,
            pressure = current.pressure,
            windSpeed = current.windSpeed,
            windDirection = current.windDeg,
            visibility = current.visibility / 1000, // conversion to km
            uvIndex = current.uvi,
            clouds = current.clouds,
            timestamp = current.dt
        ),
        hourly = hourly.take(24).map { hour ->
            HourlyWeather(
                timestamp = hour.dt,
                temperature = hour.temp,
                description = hour.weather.firstOrNull()?.description?.capitalize() ?: "",
                icon = hour.weather.firstOrNull()?.icon ?: "",
                pop = hour.pop
            )
        },
        daily = daily.map { day ->
            DailyWeather(
                date = day.dt,
                temperatureMin = day.temp.min,
                temperatureMax = day.temp.max,
                description = day.weather.firstOrNull()?.description?.capitalize() ?: "",
                icon = day.weather.firstOrNull()?.icon ?: "",
                pop = day.pop,
                humidity = day.humidity,
                windSpeed = day.windSpeed
            )
        }
    )
}

private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
