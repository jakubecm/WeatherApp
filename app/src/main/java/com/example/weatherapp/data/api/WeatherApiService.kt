package com.example.weatherapp.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    
    @GET("data/3.0/onecall")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "cz",
        @Query("exclude") exclude: String = "minutely,alerts"
    ): OneCallResponse
    
    companion object {
        const val BASE_URL = "https://api.openweathermap.org/"
    }
}
