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
    
    private val geocoder: Geocoder = Geocoder(context, Locale.forLanguageTag("cs-CZ"))
    
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
            // Zkusíme získat aktuální polohu
            var location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).await()
            
            // Pokud se nepodaří, zkusíme poslední známou polohu
            if (location == null) {
                location = fusedLocationClient.lastLocation.await()
            }
            
            if (location != null) {
                val cityName = getCityName(location.latitude, location.longitude)
                LocationResult.Success(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    cityName = cityName
                )
            } else {
                LocationResult.Error("Nepodařilo se získat polohu. Zkuste zapnout GPS.")
            }
        } catch (e: SecurityException) {
            LocationResult.Error("Chybí oprávnění k poloze")
        } catch (e: Exception) {
            LocationResult.Error("Chyba při získávání polohy: ${e.localizedMessage ?: e.message}")
        }
    }
    
    suspend fun searchLocations(query: String): List<LocationSearchResult> {
        if (query.isBlank()) return emptyList()
        
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocationName(query, 10) { addresses ->
                        val results = addresses.map { address ->
                            val cityName = address.locality ?: address.subAdminArea ?: "Neznámé místo"
                            val adminArea = address.adminArea ?: ""
                            val country = address.countryName ?: ""
                            
                            val displayName = buildString {
                                append(cityName)
                                if (adminArea.isNotEmpty() && adminArea != cityName) {
                                    append(", $adminArea")
                                }
                                if (country.isNotEmpty()) {
                                    append(", $country")
                                }
                            }
                            
                            LocationSearchResult(
                                displayName = displayName,
                                cityName = cityName,
                                latitude = address.latitude,
                                longitude = address.longitude
                            )
                        }
                        continuation.resumeWith(Result.success(results))
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(query, 10) ?: emptyList()
                addresses.map { address ->
                    val cityName = address.locality ?: address.subAdminArea ?: "Neznámé místo"
                    val adminArea = address.adminArea ?: ""
                    val country = address.countryName ?: ""
                    
                    val displayName = buildString {
                        append(cityName)
                        if (adminArea.isNotEmpty() && adminArea != cityName) {
                            append(", $adminArea")
                        }
                        if (country.isNotEmpty()) {
                            append(", $country")
                        }
                    }
                    
                    LocationSearchResult(
                        displayName = displayName,
                        cityName = cityName,
                        latitude = address.latitude,
                        longitude = address.longitude
                    )
                }
            }
        } catch (e: Exception) {
            emptyList()
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
            "%.4f, %.4f".format(latitude, longitude)
        }
    }
}

data class LocationSearchResult(
    val displayName: String,
    val cityName: String,
    val latitude: Double,
    val longitude: Double
)

sealed class LocationResult {
    data class Success(
        val latitude: Double,
        val longitude: Double,
        val cityName: String
    ) : LocationResult()
    
    data class Error(val message: String) : LocationResult()
}
