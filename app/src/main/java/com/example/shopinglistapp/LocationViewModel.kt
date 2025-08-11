package com.example.shopinglistapp

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {
    private val _location = mutableStateOf<LocationData?>(null)
    val location: State<LocationData?> = _location

    private val _address = mutableStateOf(listOf<GeocodingResult>())
    val adderss: State<List<GeocodingResult>> = _address

    fun updateLocation(newlocation: LocationData) {
        _location.value = newlocation
    }

    suspend fun fetchAddress( lat: String) {
        try {
            val result = RetrofitClient.create()
                .getAddressFormCoordinates(lat, "Your Google Map Key")
            _address.value = result.result
        } catch (e: Exception) {
            Log.d("LocationViewModel", "Error fetching address: ${e.message}")
        }
    }
}





