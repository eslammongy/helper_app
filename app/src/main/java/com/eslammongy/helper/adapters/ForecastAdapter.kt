package com.eslammongy.helper.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.databinding.WeatherLayoutBinding
import com.eslammongy.helper.data.model.MyListDaily
import com.eslammongy.helper.ui.weather.getWeatherDescriptionIconByID
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class ForecastAdapter :
    RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {

    private val colorsArray = arrayListOf("#DD2C00" , "#DD2C00" , "#9C27B0" , "#4CAF50" ,"#FF5722", "#FFBB86FC" , "#515151")
    private val diffUtil = object :DiffUtil.ItemCallback<MyListDaily>(){
        override fun areItemsTheSame(oldItem: MyListDaily, newItem: MyListDaily): Boolean {
           return oldItem.timezone == newItem.timezone
        }

        override fun areContentsTheSame(oldItem: MyListDaily, newItem: MyListDaily): Boolean {
           return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this , diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(WeatherLayoutBinding.inflate(LayoutInflater.from(parent.context) , parent , false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rnd = Random()
        val currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        holder.binding.weatherCardView.setCardBackgroundColor(currentColor)
        val forecastDailyList = differ.currentList[position]
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
        return differ.currentList.size
    }

    inner class ViewHolder(val binding:WeatherLayoutBinding) : RecyclerView.ViewHolder(binding.root)

}

