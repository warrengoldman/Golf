package com.example.golf.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MainListEntry(
    val dt: Long?=null,
    val main: Main?=null,
    val weather: List<Weather>?=listOf(),
    val clouds: Clouds?=null,
    val wind: Wind?=null,
    val visibility: Int?=null,
    val pop: Double?=null,
    val snow: Snow?=null,
    val sys: Sys?=null,
    @SerialName("dt_txt") val dtTxt: String?=null
)
