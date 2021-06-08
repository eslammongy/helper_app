package com.eslammongy.helper.commonfun

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.eslammongy.helper.R
import java.util.*

class WeatherConditions(var activity: Context) {

     fun getWeatherDescriptionIconByID(weatherID: Int , imageView: ImageView) {

        val calender = Calendar.getInstance()
        val timeOfDay = calender.get(Calendar.HOUR_OF_DAY)
        when (weatherID) {

            in 200..232 -> {
                Glide.with(activity)
                    .load(R.drawable.thunderstorm)
                    .centerCrop()
                    .into(imageView)

            }
            in 300..321 -> {
                Glide.with(activity)
                    .load(R.drawable.rainy)
                    .centerCrop()
                    .into(imageView)

            }
            in 500..531 -> {

                if (weatherID in 500..504)
                    Glide.with(activity)
                        .load(R.drawable.rain)
                        .centerCrop()
                        .into(imageView)
                else
                    Glide.with(activity)
                        .load(R.drawable.snow)
                        .centerCrop()
                        .into(imageView)

            }

            in 600..622 -> {
                Glide.with(activity)
                    .load(R.drawable.snow)
                    .centerCrop()
                    .into(imageView)

            }
            in 700..781 -> {
                Glide.with(activity)
                    .load(R.drawable.mist)
                    .centerCrop()
                    .into(imageView)

            }
            800 -> {

                if (timeOfDay in 0..18)
                    Glide.with(activity)
                        .load(R.drawable.ic_clear_sky)
                        .centerCrop()
                        .into(imageView)
                else
                    Glide.with(activity)
                        .load(R.drawable.night_sky)
                        .centerCrop()
                        .into(imageView)


            }
            in 800..804 -> {

                when (weatherID) {
                    801 -> Glide.with(activity)
                        .load(R.drawable.few_cloud)
                        .centerCrop()
                        .into(imageView)
                    802 -> Glide.with(activity)
                        .load(R.drawable.scattered_clouds)
                        .centerCrop()
                        .into(imageView)
                    else -> Glide.with(activity)
                        .load(R.drawable.broken_clouds)
                        .centerCrop()
                        .into(imageView)
                }

            }

        }


    }


}



