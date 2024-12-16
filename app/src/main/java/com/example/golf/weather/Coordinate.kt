package com.example.golf.weather

import kotlinx.serialization.Serializable

@Serializable
data class Coordinate(val lat: Double?=null, val lon: Double?=null)
