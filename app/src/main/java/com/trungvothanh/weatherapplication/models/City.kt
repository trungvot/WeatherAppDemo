package com.trungvothanh.weatherapplication.models

data class City(
    val coord: Coordinate,
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val sys: Sys,
    val name: String = "",
) {
}

