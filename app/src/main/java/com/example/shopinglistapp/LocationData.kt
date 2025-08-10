package com.example.shopinglistapp

data class LocationData(
    val latitude: Double,
    val longitude: Double,
//    val address: String
)

data class GeocodingResopnse(
    val result: List<GeocodingResult>,
    val status: String
)

data class GeocodingResult(
    val formatted_address: String,
)
