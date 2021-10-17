package com.eslammongy.helper.repository

import com.eslammongy.helper.remoteApi.WeatherService
import retrofit2.http.Query

class WeatherRepo (private val weatherService: WeatherService) {

    fun getCurrentWeatherStatus(@Query("lat") lat: String,
                                @Query("lon") lon: String,
                                @Query("APPID") app_id: String,
                                @Query("units") units: String) = weatherService.getCurrentWeatherStatus(lat , lon , app_id , units)


    fun getDailyWeatherStatus (@Query("lat") lat: String,
                                   @Query("lon") lon: String,
                                   @Query("exclude") exclude:String,
                                   @Query("APPID") app_id: String,
                                   @Query("units") units: String) = weatherService.getDailyWeatherStatus(lat , lon , exclude , app_id , units)
}