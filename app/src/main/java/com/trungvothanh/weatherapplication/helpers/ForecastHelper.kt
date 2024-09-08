package com.trungvothanh.weatherapplication.helpers

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.trungvothanh.weatherapplication.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class ForecastHelper(val context: Context) {


    fun getBackground(icon: String): Drawable {
        when (icon) {
            "clear sky" -> {
                return ContextCompat.getDrawable(context, R.drawable.clear_day_bg)!!
            }
            "rain" -> {
                return ContextCompat.getDrawable(context, R.drawable.rain_bg)!!
            }
            "heavy intensity rain" -> {
                return ContextCompat.getDrawable(context, R.drawable.rain_bg)!!
            }
            "moderate rain" -> {
                return ContextCompat.getDrawable(context, R.drawable.rain_bg)!!
            }
            "snow" -> {
                return ContextCompat.getDrawable(context, R.drawable.snow_bg)!!
            }
            "few clouds" -> {
                return ContextCompat.getDrawable(context, R.drawable.sleet_bg)!!
            }
            "thunderstorm" -> {
                return ContextCompat.getDrawable(context, R.drawable.thunderstorm_bg)!!
            }
            "shower rain" -> {
                return ContextCompat.getDrawable(context, R.drawable.snow_bg)!!
            }
            "cloudy" -> {
                return ContextCompat.getDrawable(context, R.drawable.cloudy_bg)!!
            }
            "scattered clouds" -> {
                return ContextCompat.getDrawable(context, R.drawable.partly_cloudy_day_bg)!!
            }
            "broken clouds" -> {
                return ContextCompat.getDrawable(context, R.drawable.partly_cloudy_night_bg)!!
            }
            "mist" -> {
                return ContextCompat.getDrawable(context, R.drawable.hail_bg)!!
            }
            else -> {
                return ContextCompat.getDrawable(context, R.drawable.clear_day_bg)!!
            }
        }
    }

    //get short name of a day from timezone. Example: Fri
    @RequiresApi(Build.VERSION_CODES.O)
    fun convertLongToTime(time: Long): String {
        val instant = Instant.ofEpochSecond(time)
        // Create a DateTimeFormatter with the desired pattern
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault()) // Use system's default time zone
        // Format the Instant to a string
        return formatter.format(instant)
    }
}