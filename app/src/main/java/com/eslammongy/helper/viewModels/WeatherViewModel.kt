package com.eslammongy.helper.viewModels

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.eslammongy.helper.model.MyListDaily
import com.eslammongy.helper.model.Weather
import com.eslammongy.helper.model.WeatherResponse
import com.eslammongy.helper.remoteApi.RetrofitBuilder
import com.eslammongy.helper.repository.WeatherRepo
import com.eslammongy.helper.ui.module.weather.connectingError
import com.eslammongy.helper.utilis.apiKey
import com.eslammongy.helper.utilis.setToastMessage
import com.eslammongy.helper.utilis.showingSnackBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Query

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    val dailyWeatherList: MutableLiveData<List<MyListDaily>> = MutableLiveData<List<MyListDaily>>()
    val currentWeatherList: MutableLiveData<WeatherResponse> = MutableLiveData()
    private val weatherRepo: WeatherRepo

    init {
        val service = RetrofitBuilder.ApiServices
        weatherRepo = WeatherRepo(service)
    }

    fun getCurrentWeatherState(
        @Query("lat") lat: String,
        @Query("lon") lon: String,view: View
    ):LiveData<WeatherResponse> {
        val apiCall = weatherRepo.getCurrentWeatherStatus(lat , lon , apiKey, "metric")
         viewModelScope.launch(Dispatchers.IO)  {
             apiCall.enqueue(object : Callback<WeatherResponse> {
                 @SuppressLint("SetTextI18n")
                 override fun onResponse(call: Call<WeatherResponse>?, response: Response<WeatherResponse>?) {

                     if (response!!.code() in 200..299){
                         currentWeatherList.postValue(response.body())
                     }else{
                         connectingError(view , response.code())
                     }
                 }
                 override fun onFailure(call: Call<WeatherResponse>?, t: Throwable?) {
                     connectingError(view , 404)
                 }
             })
         }
        return  currentWeatherList
    }


    fun getDialWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
    ): MutableLiveData<List<MyListDaily>> {

        val apiResponse = weatherRepo.getDailyWeatherStatus(
            lat, lon, "hourly,minutely,current,alerts", apiKey, "metric"
        )
        viewModelScope.launch(Dispatchers.IO) {
            apiResponse.enqueue(object : Callback<MyListDaily> {
                override fun onResponse(
                    call: Call<MyListDaily>,
                    response: Response<MyListDaily>) {
                    val listDaily = arrayListOf<MyListDaily>()
                    for (item in 0..6) {
                        listDaily.add(response.body()!!)
                    }
                    dailyWeatherList.postValue(listDaily)
                }

                override fun onFailure(call: Call<MyListDaily>, t: Throwable) {
                    dailyWeatherList.postValue(null)
                }

            })
        }
        return dailyWeatherList
    }


}