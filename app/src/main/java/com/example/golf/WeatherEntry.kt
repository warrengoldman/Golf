package com.example.golf

data class WeatherEntry(
    val sunsetFormattedTime: String,
    val temp: Double,
    val heading: String,
    val dt: Long,
    val skyTitle: String,
    val skyDescription: String,
    val skyIcon: String,
    val windMph: Double,
    val windGustMph: Double,
    val windDirection: String
)