package com.example.weatherapp.data.repository

import com.example.weatherapp.data.api.WeatherApiService
import com.example.weatherapp.data.mapper.toWeatherData
import com.example.weatherapp.data.model.WeatherData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class WeatherRepository {
    
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(WeatherApiService.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
    
    private val apiService = retrofit.create(WeatherApiService::class.java)
    
    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        locationName: String
    ): Result<WeatherData> {
        return try {
            val response = apiService.getWeather(
                latitude = latitude,
                longitude = longitude,
                apiKey = com.example.weatherapp.BuildConfig.OPENWEATHERMAP_API_KEY
            )
            
            val weatherData = response.toWeatherData(locationName)
            Result.success(weatherData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
