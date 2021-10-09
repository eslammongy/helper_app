package com.eslammongy.helper.remoteApi

import com.eslammongy.helper.model.MyListDaily
import com.eslammongy.helper.model.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("data/2.5/weather?")
    fun getCurrentWeatherStatus (@Query("lat") lat: String,
                                 @Query("lon") lon: String,
                                 @Query("APPID") app_id: String,
                                 @Query("units") units: String): Call<WeatherResponse>


    @GET("data/2.5/onecall?")
    fun getDailyWeatherStatus (@Query("lat") lat: String,
                               @Query("lon") lon: String,
                               @Query("exclude") exclude:String,
                               @Query("APPID") app_id: String,
                               @Query("units") units: String): Call<MyListDaily>

}
