package com.trungvothanh.weatherapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trungvothanh.weatherapplication.models.City
import com.trungvothanh.weatherapplication.models.Coordinate
import com.trungvothanh.weatherapplication.models.Main
import com.trungvothanh.weatherapplication.models.Sys
import com.trungvothanh.weatherapplication.models.Weather
import com.trungvothanh.weatherapplication.models.Wind
import com.trungvothanh.weatherapplication.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalCoroutinesApi
@FlowPreview
@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherRepository: WeatherRepository) :
    ViewModel() {
    private val _weatherResponse: MutableStateFlow<City> = MutableStateFlow(
        City(
            Coordinate(),
            listOf(Weather()),
            Main(),
            Wind(),
            Sys()
        )
    )
    val weatherResponse: StateFlow<City> = _weatherResponse

    private val _error: MutableStateFlow<String?> = MutableStateFlow(null)
    val error: StateFlow<String?> = _error

    private val searchChannel = MutableSharedFlow<String>(1)


    fun setSearchQuery(search: String) {
        searchChannel.tryEmit(search)
    }

    init {
        getCityData()
    }

    private fun getCityData() {
        viewModelScope.launch {
            searchChannel
                .flatMapLatest { search ->
                    weatherRepository.getCityData(search)
                }.catch { e ->
                    _error.value = e.message
                    _weatherResponse.value = City(
                        Coordinate(), listOf(Weather()), Main(), Wind(), Sys()
                    ) // Reset to a default state on error
                }.collect { response ->
                    _weatherResponse.value = response
                    _error.value = null // Clear error if successful
                }
        }
    }
}