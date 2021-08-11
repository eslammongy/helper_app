package com.eslammongy.helper.ui.module.weather

import android.app.Activity
import android.view.View
import android.widget.ImageView
import com.eslammongy.helper.R
import com.eslammongy.helper.helpers.GlideApp
import com.eslammongy.helper.helpers.showingSnackBar
import java.util.*

     fun Activity.getWeatherDescriptionIconByID(weatherID: Any , imageView: ImageView) {

        val calender = Calendar.getInstance()
        val timeOfDay = calender.get(Calendar.HOUR_OF_DAY)
        when (weatherID) {

            in 200..232 -> {
                GlideApp.with(this).asBitmap()
                    .load(R.drawable.ic_thunderstorm)
                    .into(imageView).clearOnDetach()

            }
            in 300..321 -> {
                GlideApp.with(this).asBitmap()
                    .load(R.drawable.ic_snower_rain)
                    .into(imageView).clearOnDetach()

            }
            in 500..531 -> {

                if (weatherID in 500..504)
                    GlideApp.with(this).asBitmap()
                        .load(R.drawable.ic_rainy)
                        .into(imageView).clearOnDetach()
                else
                    GlideApp.with(this).asBitmap()
                        .load(R.drawable.ic_snower_rain)
                        .into(imageView).clearOnDetach()

            }

            in 600..622 -> {
                GlideApp.with(this).asBitmap()
                    .load(R.drawable.ic_snowflake)
                    .into(imageView).clearOnDetach()

            }
            in 700..781 -> {
                GlideApp.with(this).asBitmap()
                    .load(R.drawable.ic_foggy)
                    .into(imageView).clearOnDetach()

            }
            800 -> {

                if (timeOfDay in 0..18)
                    GlideApp.with(this).asBitmap()
                        .load(R.drawable.ic_clear_sky)
                        .into(imageView).clearOnDetach()
                else
                    GlideApp.with(this).asBitmap()
                        .load(R.drawable.ic_night)
                        .into(imageView).clearOnDetach()


            }
            in 800..804 -> {

                if (weatherID == 801) GlideApp.with(this).asBitmap()
                    .load(R.drawable.ic_few_clouds)
                    .into(imageView).clearOnDetach()
                else if (weatherID == 802) GlideApp.with(this).asBitmap()
                    .load(R.drawable.ic_scattered_clouds)
                    .into(imageView).clearOnDetach()
                else GlideApp.with(this).asBitmap()
                    .load(R.drawable.ic_broken_clouds)
                    .into(imageView).clearOnDetach()

            }

        }


    }

fun connectingError(view:View ,errorCode:Int){
    when(errorCode){
        in 100..199 -> {

            showingSnackBar(view , "Your Session has expired." , "#DD2C00")
        }
        in 300..399 -> {

            showingSnackBar(view , "Redirection messages." , "#DD2C00")
        }
        in 400..499 -> {

            showingSnackBar(view , "The server could not understand the request ..Bad Request" , "#DD2C00")
        }
        else -> {

            showingSnackBar(view , "Server error responses when connecting" , "#DD2C00")
        }
    }
}







