package com.eslammongy.helper.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.databinding.WeatherLayoutBinding
import com.eslammongy.helper.ui.module.weather.getWeatherDescriptionIconByID
import com.eslammongy.helper.model.MyListDaily
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class ForecastAdapter(private var dailyForecastList: ArrayList<MyListDaily>) :
    RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(WeatherLayoutBinding.inflate(LayoutInflater.from(parent.context) , parent , false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val forecastDailyList = dailyForecastList[position]
        val datedAt = forecastDailyList.daily[position].dt
        val updatedText = SimpleDateFormat("EEEE,dd", Locale.ENGLISH).format(Date(datedAt.toLong() * 1000))
        holder.binding.dailyDayName.text = updatedText
        val iconId = forecastDailyList.daily[position].weather[0].id
        (holder.binding.root.context as Activity).getWeatherDescriptionIconByID(iconId , holder.binding.dailyWeatherIcon)
        val desc = forecastDailyList.daily[position].weather[0].description
        holder.binding.dailyWeatherStatus.text = desc

        val currentTemp = forecastDailyList.daily[position].temp.day.roundToInt()
        holder.binding.dailyWeatherTemp.text = "${currentTemp}°C"

    }

    override fun getItemCount(): Int {
        return dailyForecastList.size
    }

    inner class ViewHolder(val binding:WeatherLayoutBinding) : RecyclerView.ViewHolder(binding.root)

}

