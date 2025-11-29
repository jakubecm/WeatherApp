package com.example.weatherapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "weather_preferences")

class PreferencesManager(private val context: Context) {
    
    companion object {
        private val HOME_LOCATION_NAME = stringPreferencesKey("home_location_name")
        private val HOME_LOCATION_LAT = stringPreferencesKey("home_location_lat")
        private val HOME_LOCATION_LON = stringPreferencesKey("home_location_lon")
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
    }
    
    val homeLocation: Flow<HomeLocation?> = context.dataStore.data.map { preferences ->
        val name = preferences[HOME_LOCATION_NAME]
        val lat = preferences[HOME_LOCATION_LAT]
        val lon = preferences[HOME_LOCATION_LON]
        
        if (name != null && lat != null && lon != null) {
            HomeLocation(name, lat, lon)
        } else {
            null
        }
    }
    
    suspend fun saveHomeLocation(location: HomeLocation) {
        context.dataStore.edit { preferences ->
            preferences[HOME_LOCATION_NAME] = location.name
            preferences[HOME_LOCATION_LAT] = location.latitude
            preferences[HOME_LOCATION_LON] = location.longitude
        }
    }
    
    suspend fun clearHomeLocation() {
        context.dataStore.edit { preferences ->
            preferences.remove(HOME_LOCATION_NAME)
            preferences.remove(HOME_LOCATION_LAT)
            preferences.remove(HOME_LOCATION_LON)
        }
    }
    
    val darkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DARK_MODE] ?: false
    }
    
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE] = enabled
        }
    }
}

data class HomeLocation(
    val name: String,
    val latitude: String,
    val longitude: String
)
