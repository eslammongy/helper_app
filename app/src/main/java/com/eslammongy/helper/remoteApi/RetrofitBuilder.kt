package com.eslammongy.helper.remoteApi

import com.eslammongy.helper.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {

    private const val baseUrl = "http://api.openweathermap.org/"
    private val retrofit by lazy{
         Retrofit.Builder()
            .baseUrl(baseUrl)
             .client(OkHttpClient.Builder().also { okHttpClient ->
                 if (BuildConfig.DEBUG){
                     val logging = HttpLoggingInterceptor()
                     logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                     okHttpClient.connectTimeout(10, TimeUnit.SECONDS)
                     okHttpClient.readTimeout(10, TimeUnit.SECONDS)
                     okHttpClient.writeTimeout(10, TimeUnit.SECONDS)
                     okHttpClient.addInterceptor(logging)
                 }
             }.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val ApiServices: WeatherService by lazy { retrofit.create(WeatherService::class.java) }

}