package com.eslammongy.helper.data.model

data class MyListDaily(
    val daily: List<Daily>,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int
)