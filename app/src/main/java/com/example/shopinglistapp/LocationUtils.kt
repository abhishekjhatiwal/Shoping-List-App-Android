package com.example.shopinglistapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

@SuppressLint("MissingPermission")
class LocationUtiles(val context: Context) {
    val _fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun requestLocationUpdate(viewModel: LocationViewModel) {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    val locationData = LocationData(it.latitude, it.longitude)
                    viewModel.updateLocation(locationData)
                }
            }
        }
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()

        _fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun hasLocationPermission(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

//    fun reverseGeocodeLocation(location: LocationData): String {
//        val geocoder = Geocoder(context, Locale.getDefault())
//        val coordinate = LatLng(location.latitude, location.longitude)
//        val address: MutableList<android.location.Address>? =
//            geocoder.getFromLocation(coordinate.latitude, coordinate.longitude, 1)
//        return if (address?.isNotEmpty() == true) {
//            address[0].getAddressLine(0)
//        }else{
//            "Address Not found"
//        }
//    }
}