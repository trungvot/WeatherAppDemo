package com.trungvothanh.weatherapplication.repository

import com.trungvothanh.weatherapplication.helpers.Constants
import com.trungvothanh.weatherapplication.models.City
import com.trungvothanh.weatherapplication.network.ApiServiceImp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val apiServiceImp: ApiServiceImp) {

    fun getCityData(city: String): Flow<City> = flow {
        val response = apiServiceImp.getCity(city, Constants.APP_ID_KEY)
        emit(response)
    }.flowOn(Dispatchers.IO)
        .conflate()
}