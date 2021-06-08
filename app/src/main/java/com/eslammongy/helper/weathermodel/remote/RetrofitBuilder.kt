package com.eslammongy.helper.weathermodel.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    private const val baseUrl = "http://api.openweathermap.org/"
    private val retrofit by lazy{
         Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val ApiServices: WeatherService by lazy { retrofit.create(WeatherService::class.java) }

}