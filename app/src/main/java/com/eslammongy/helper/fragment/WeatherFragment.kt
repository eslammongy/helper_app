package com.eslammongy.helper.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.eslammongy.helper.adapters.ForecastAdapter
import com.eslammongy.helper.databinding.FragmentWeatherBinding
import com.eslammongy.helper.helperfun.UserPermission
import com.eslammongy.helper.helperfun.WeatherConditions
import com.eslammongy.helper.weathermodel.WeatherResponse
import com.eslammongy.helper.weathermodel.dailyforecast.MyListDaily
import com.eslammongy.helper.weathermodel.remote.RetrofitBuilder
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
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
    private val userLocation by lazy { UserPermission(requireActivity()) }
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private val weatherConditions by lazy { WeatherConditions(requireActivity()) }
    private lateinit var lats: String
    lateinit var lang: String
    var listOfDailyForecast = ArrayList<MyListDaily>()
    private val forecastAdapter by lazy { ForecastAdapter(requireActivity(), listOfDailyForecast) }

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

        sharedPreferences = requireActivity().getSharedPreferences("UserLocation", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        lats = sharedPreferences.getString("UserLat", "Noun").toString()
        lang = sharedPreferences.getString("UserLang", "Noun").toString()

        disableView()
        if (userLocation.checkUserLocationPermission()){
            getCurrentWeatherDate(lats, lang)
            getWeatherDaily(lats, lang)

        }else{
            userLocation.checkUserPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                "Location",
                requestPermissionCode
            )
            enableView("Default")
        }

        binding.reFreshLayout.setOnRefreshListener {
            binding.reFreshLayout.isRefreshing = false
           getCurrentLocation()

        }
    }

    private fun disableView() {
        binding.circularProgressBar.visibility = View.VISIBLE
        binding.parentView.alpha = 0.3F
    }

    private fun enableView(errorView: String) {
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
                Toast.makeText(requireActivity(), "Location permission refused", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(requireActivity(), "Location permission granted", Toast.LENGTH_LONG)
                    .show()
                getCurrentLocation()
            }

    }

    private fun getCurrentWeatherDate(lats: String, longs: String) {
        disableView()
        RetrofitBuilder.ApiServices.getCurrentWeatherStatus(lats, longs, apiKey, "metric")
            .enqueue(object : Callback<WeatherResponse> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<WeatherResponse>?,
                    response: Response<WeatherResponse>?
                ) {
                    enableView("Default")
                    if (response!!.code() == 200) {
                        val weatherResponse = response.body()!!
                        binding.tvCityName.text =
                            weatherResponse.name + "${weatherResponse.sys!!.country}"

                        val weatherStatusID = weatherResponse.weather[0].id
                        weatherConditions.getWeatherDescriptionIconByID(
                            weatherStatusID,
                            binding.weatherStatusImage
                        )

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

                    } else {
                        enableView("Error")
                        Toast.makeText(
                            activity,
                            "Your Session has expired.\n${response.message()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>?, t: Throwable?) {
                    enableView("Error")
                    Toast.makeText(
                        activity,
                        "Error Occurred when connecting.\n${t!!.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            })

    }

    private fun getWeatherDaily(lats: String, longs: String) {

      RetrofitBuilder.ApiServices.getDailyWeatherStatus(
          lats,
          longs,
          "hourly,minutely,current,alerts",
          apiKey,
          "metric"
      ).enqueue(object : Callback<MyListDaily> {

          override fun onResponse(call: Call<MyListDaily>?, response: Response<MyListDaily>?) {
              val dailyResponse = response!!.body()
              if (response.code() == 200) {
                  for (item in 0..7) {
                      listOfDailyForecast.add(dailyResponse!!)
                  }

                  displayDailyRecyclerView()
              } else {
                  Toast.makeText(
                      requireActivity(),
                      "Your Session has expired.\n${response.message()}",
                      Toast.LENGTH_LONG
                  ).show()
                  enableView("Error")
              }

          }

          override fun onFailure(call: Call<MyListDaily>?, t: Throwable?) {
              enableView("Error")
              Toast.makeText(
                  activity,
                  "Error Occurred when connecting.\n${t!!.message}",
                  Toast.LENGTH_LONG
              ).show()

          }
      })

    }
    private fun displayDailyRecyclerView(){
        binding.weatherRecyclerView.setHasFixedSize(true)
        binding.weatherRecyclerView.hasFixedSize()
        binding.weatherRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.weatherRecyclerView.adapter = forecastAdapter
    }

    @SuppressLint("CommitPrefEdits", "MissingPermission")
    fun getCurrentLocation() {
        val sharedPreferences = requireActivity().getSharedPreferences(
            "UserLocation",
            Context.MODE_PRIVATE
        )
        editor = sharedPreferences.edit()
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        LocationServices.getFusedLocationProviderClient(requireActivity())
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                @Override
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(activity!!)
                        .removeLocationUpdates(this)
                    if (locationResult.locations.size > 0) {
                        val locationIndex = locationResult.locations.size - 1
                        lats = locationResult.locations[locationIndex].latitude.toString()
                        lang = locationResult.locations[locationIndex].longitude.toString()
                        getCurrentWeatherDate(lats, lang)
                        getWeatherDaily(lats, lang)
                        editor.apply {
                            putString("UserLat", lats)
                            putString("UserLang", lang)
                            //Toast.makeText(activity, "$lats ... $lang inside if getCurrentLocation", Toast.LENGTH_LONG).show()
                            apply()
                        }

                    }
                }

            }, Looper.getMainLooper())

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}
