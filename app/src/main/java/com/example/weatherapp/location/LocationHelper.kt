package com.example.weatherapp.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.Locale
import kotlin.coroutines.resume

class LocationHelper(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    private val geocoder: Geocoder = Geocoder(context, Locale.getDefault())
    
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    suspend fun getCurrentLocation(): LocationResult {
        if (!hasLocationPermission()) {
            return LocationResult.Error("Chybí oprávnění k poloze")
        }
        
        return try {
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                CancellationTokenSource().token
            ).await()
            
            if (location != null) {
                val cityName = getCityName(location.latitude, location.longitude)
                LocationResult.Success(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    cityName = cityName
                )
            } else {
                LocationResult.Error("Nepodařilo se získat polohu")
            }
        } catch (e: Exception) {
            LocationResult.Error("Chyba při získávání polohy: ${e.message}")
        }
    }
    
    private suspend fun getCityName(latitude: Double, longitude: Double): String {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Pro Android 13+ - použijeme suspend funkci s continuation
                kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                        val cityName = if (addresses.isNotEmpty()) {
                            addresses[0].locality 
                                ?: addresses[0].subAdminArea 
                                ?: addresses[0].adminArea
                                ?: "Neznámé místo"
                        } else {
                            "Neznámé místo"
                        }
                        continuation.resumeWith(Result.success(cityName))
                    }
                }
            } else {
                // Pro starší verze
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    addresses[0].locality 
                        ?: addresses[0].subAdminArea 
                        ?: addresses[0].adminArea
                        ?: "Neznámé místo"
                } else {
                    "Neznámé místo"
                }
            }
        } catch (e: Exception) {
            // Pokud geocoding selže, vrátíme alespoň souřadnice
            "%.4f, %.4f".format(latitude, longitude)
        }
    }
}

sealed class LocationResult {
    data class Success(
        val latitude: Double,
        val longitude: Double,
        val cityName: String
    ) : LocationResult()
    
    data class Error(val message: String) : LocationResult()
}
