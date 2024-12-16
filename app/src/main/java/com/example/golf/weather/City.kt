package com.example.golf.weather

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val id: Long?=null,
    val name: String?=null,
    val coord: Coordinate?=null,
    val country: String?=null,
    val population: Int?=null,
    val timezone: Int?=null,
    val sunrise: Long?=null,
    val sunset: Long?=null
)
