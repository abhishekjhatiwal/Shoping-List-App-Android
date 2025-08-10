package com.example.shopinglistapp

import retrofit2.http.GET
import retrofit2.http.Query

interface GeoCodingApiService {
    @GET("maps/api/geocode/json")
    suspend fun getAddressFormCoordinates(
        @Query("latitude") latitude: String,
        @Query("key") apiKey: String,
    ): GeocodingResopnse
}