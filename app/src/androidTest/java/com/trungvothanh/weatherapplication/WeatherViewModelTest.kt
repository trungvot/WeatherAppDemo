package com.trungvothanh.weatherapplication

import com.trungvothanh.weatherapplication.models.City
import com.trungvothanh.weatherapplication.models.Coordinate
import com.trungvothanh.weatherapplication.models.Main
import com.trungvothanh.weatherapplication.models.Sys
import com.trungvothanh.weatherapplication.models.Weather
import com.trungvothanh.weatherapplication.models.Wind
import com.trungvothanh.weatherapplication.repository.WeatherRepository
import com.trungvothanh.weatherapplication.viewmodel.WeatherViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var weatherViewModel: WeatherViewModel

    @Before
    fun setup() {
        weatherRepository = mockk()
        weatherViewModel = WeatherViewModel(weatherRepository)
    }

    @Test
    fun weatherViewModel_GetWeatherInfo_GetWeatherInfoSuccess() = runTest {
        // Arrange
        val city = City(Coordinate(), listOf(Weather()), Main(), Wind(), Sys())
        coEvery { weatherRepository.getCityData("London") } returns flow { emit(city) }

        // Act
        weatherViewModel.setSearchQuery("London")

        // Assert
        val result = weatherViewModel.weatherResponse.first()
        assert(result == city)
        coVerify { weatherRepository.getCityData("London") }
    }

    @Test
    fun weatherViewModel_GetWeatherInfo_GetWeatherInfoFail() = runTest {
        // Arrange
        val errorMessage = "Error fetching data"
        coEvery { weatherRepository.getCityData("Test City") } returns flow { throw Exception(errorMessage) }

        // Act
        weatherViewModel.setSearchQuery("Test City")

        // Assert
        val result = weatherViewModel.weatherResponse.first()
        assert(result == City(Coordinate(), listOf(Weather()), Main(), Wind(), Sys())) // Check if default state is set on error
        // No error handling in the provided ViewModel, adjust as necessary
        coVerify { weatherRepository.getCityData("Test City") }
    }
}
