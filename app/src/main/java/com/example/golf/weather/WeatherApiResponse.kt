package com.example.golf.weather

import kotlinx.serialization.Serializable

@Serializable
data class WeatherApiResponse(
    val cod: String? = null,
    val message: Int? = null,
    val cnt: Int? = null,
    val list: List<MainListEntry>? = listOf(),
    val city: City? = null
)
