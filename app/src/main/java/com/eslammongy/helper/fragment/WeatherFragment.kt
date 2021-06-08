package com.eslammongy.helper.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.eslammongy.helper.adapters.ForecastAdapter
import com.eslammongy.helper.commonfun.UserPermission
import com.eslammongy.helper.commonfun.WeatherConditions
import com.eslammongy.helper.databinding.FragmentWeatherBinding
import com.eslammongy.helper.weathermodel.WeatherResponse
import com.eslammongy.helper.weathermodel.dailyforecast.MyListDaily
import com.eslammongy.helper.weathermodel.remote.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private val apiKey: String = "ca4f047ebf37d1d9d7dfebb4100bd981"
    private var requestPermissionCode: Int = 10
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var weatherConditions: WeatherConditions
    var listOfDailyForecast = ArrayList<MyListDaily>()
    private val forecastAdapter by lazy { ForecastAdapter(activity!! , listOfDailyForecast) }
    private val userLocation by lazy { UserPermission(activity!!) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("CommitPrefEdits")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weatherConditions = WeatherConditions(activity!!)
        sharedPreferences = activity!!.getSharedPreferences("UserLocation", Context.MODE_PRIVATE)
        val lats = sharedPreferences.getString("UserLat", null)
        val lang = sharedPreferences.getString("UserLang", null)
        disableView()
        if (userLocation.checkUserLocationPermission()){
            getCurrentWeatherDate(lats.toString(), lang.toString())
            getWeatherDaily(lats.toString(), lang.toString())

        }else{
             userLocation.checkUserPermission(Manifest.permission.ACCESS_FINE_LOCATION , "Location" , requestPermissionCode)
             enableView("Default")
        }

    }

    private fun disableView() {
        binding.circularProgressBar.visibility = View.VISIBLE
        binding.parentView.alpha = 0.3F
    }

    private fun enableView(errorView:String) {
        binding.circularProgressBar.visibility = View.GONE
        if (errorView == "Error"){
            binding.parentView.visibility = View.GONE
            binding.emptyImageView.visibility = View.VISIBLE
        }
        binding.parentView.alpha = 1.0F
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity!!, "Location permission refused", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(activity!!, "Location permission granted", Toast.LENGTH_LONG)
                    .show()
                userLocation.getCurrentLocation()
            }

    }

    private fun getCurrentWeatherDate(lats: String, longs: String) {
        disableView()
        RetrofitBuilder.ApiServices.getCurrentWeatherStatus(lats, longs, apiKey, "metric")
            .enqueue(object : Callback<WeatherResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<WeatherResponse>?, response: Response<WeatherResponse>?) {
                enableView("Default")
                if (response!!.code() == 200) {
                    val weatherResponse = response.body()!!
                    binding.tvCityName.text =
                        weatherResponse.name + "${weatherResponse.sys!!.country}"

                    val weatherStatusID = weatherResponse.weather[0].id
                    weatherConditions.getWeatherDescriptionIconByID(weatherStatusID, binding.weatherStatusImage)

                    val weatherStatusDec = weatherResponse.weather[0].description
                    binding.tvWeatherStatus.text = weatherStatusDec.toString()

                    val updatedAt = response.body()!!.dt.toLong()
                    val updatedAtText = "Updated at: " + SimpleDateFormat(
                        "dd/MM/yyyy hh:mm a", Locale.ENGLISH
                    ).format(Date(updatedAt * 1000))
                    binding.tvLastUpdate.text = updatedAtText

                    val currentTemp = response.body()!!.main!!.temp.roundToInt()
                    binding.tvWeatherTempDegree.text = "${currentTemp}°C"

                    val minTemp = response.body()!!.main!!.temp_min.roundToInt()
                    binding.tvMinTemp.text = "Min $minTemp ºC"
                    val maxTemp = response.body()!!.main!!.temp_max.roundToInt()
                    binding.tvMaxTemp.text = "Max $maxTemp ºC"

                }
                else {
                    enableView("Error")
                    Toast.makeText(activity, "Your Session has expired.\n${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>?, t: Throwable?) {
                enableView("Error")
                Toast.makeText(activity, "Error Occurred when connecting.\n${t!!.message}", Toast.LENGTH_LONG).show()
            }

        })


    }

    private fun getWeatherDaily(lats: String, longs: String) {

      RetrofitBuilder.ApiServices.getDailyWeatherStatus( lats,
          longs,
          "hourly,minutely,current,alerts",
          apiKey,
          "metric").enqueue(object : Callback<MyListDaily> {

            override fun onResponse(call: Call<MyListDaily>?, response: Response<MyListDaily>?) {
                val dailyResponse = response!!.body()
                if (response.code() == 200) {
                       for (item in 0..6) {
                           listOfDailyForecast.add(dailyResponse!!) }
                           displayDailyRecyclerView()
                }
                else {
                    Toast.makeText(activity, "Your Session has expired.\n${response.message()}", Toast.LENGTH_LONG).show()
                    enableView("Error")
                }

            }

            override fun onFailure(call: Call<MyListDaily>?, t: Throwable?) {
                enableView("Error")
                Toast.makeText(activity, "Error Occurred when connecting.\n${t!!.message}", Toast.LENGTH_LONG).show()

            }
        })

    }
    private fun displayDailyRecyclerView(){
        binding.weatherRecyclerView.setHasFixedSize(true)
        binding.weatherRecyclerView.hasFixedSize()
        binding.weatherRecyclerView.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        binding.weatherRecyclerView.adapter = forecastAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}