package com.trungvothanh.weatherapplication.network

import com.trungvothanh.weatherapplication.models.City
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather")
    suspend fun getCityData(
        @Query("q") city: String,
        @Query("appid") appId:String,
        @Query("units") units: String = "metric"
    ): City
}