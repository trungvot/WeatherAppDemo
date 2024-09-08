package com.trungvothanh.weatherapplication.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.trungvothanh.weatherapplication.databinding.ForecastActivityBinding
import com.trungvothanh.weatherapplication.helpers.Constants
import com.trungvothanh.weatherapplication.helpers.ForecastHelper
import com.trungvothanh.weatherapplication.models.City
import com.trungvothanh.weatherapplication.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class ForecastActivity : AppCompatActivity() {
    private lateinit var binding: ForecastActivityBinding
    private val weatherViewModel: WeatherViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ForecastActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get passed data
        val cityName = intent.getStringExtra(Constants.CITY_NAME)

        lifecycleScope.launch {
            cityName?.let { weatherViewModel.setSearchQuery(it) }
        }

        lifecycleScope.launch {
            weatherViewModel.weatherResponse.collect { response ->
                if (response.coord.lat != 0.0 && response.coord.lon != 0.0) {
                    populateUIWithForecastData(response)
                }
            }
        }

        /* BUTTONS */
        binding.backToLocationListBtn.setOnClickListener {
            backToLocationListBtn()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun populateUIWithForecastData(city: City) {

        val forecastHelper = ForecastHelper(this)

        //update UI
        binding.forecastScrollView.background =
            forecastHelper.getBackground(city.weather[0].description)
        binding.localityNameTxt.text = city.name
        binding.forecastSummaryTxt.text = city.weather[0].description
        Glide.with(this@ForecastActivity)
            .load("https://openweathermap.org/img/wn/${city.weather[0].icon}@2x.png")
            .into(binding.forecastIcon)
        binding.forecastCurrentTemperatureTxt.text = "${city.main.temp}°"
        binding.forecastMaxTemperatureTxt.text = "${city.main.temp_max}°"
        binding.forecastMinTemperatureTxt.text = "${city.main.temp_min}°"

        //detail outles
        binding.uvIndexTxt.text = forecastHelper.convertLongToTime(city.sys.sunrise)
        binding.chanceOfRainTtxt.text = forecastHelper.convertLongToTime(city.sys.sunset)
        binding.windSpeedTxt.text = "${city.wind.speed} km/h"
        binding.humidiyTxt.text = "${(city.main.humidity)}%"
        binding.pressureTtxt.text = "${city.main.pressure} hPa"
        binding.dewPointTxt.text = "${city.wind.deg}°"
    }

    private fun backToLocationListBtn() {
        //redirect to location list activity
        val intent = Intent(this, LocationListActivity::class.java)
        startActivity(intent)
    }
}