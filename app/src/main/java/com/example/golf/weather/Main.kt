package com.example.golf.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Main(
    val temp: Double?=null,
    @SerialName("feels_like") val feelsLike: Double?=null,
    @SerialName("temp_min") val tempMin: Double?=null,
    @SerialName("temp_max") val tempMax: Double?=null,
    val pressure: Int?=null,
    @SerialName("sea_level") val seaLevel: Int?=null,
    @SerialName("grnd_level") val grndLevel: Int?=null,
    val humidity: Int?=null,
    @SerialName("temp_kf") val tempKf: Double?=null
)
