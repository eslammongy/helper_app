package com.eslammongy.helper.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.eslammongy.helper.adapters.ForecastAdapter
import com.eslammongy.helper.commonfun.DailyForeCast
import com.eslammongy.helper.databinding.FragmentWeatherBinding
import com.eslammongy.helper.weathermodel.WeatherResponse
import com.eslammongy.helper.weathermodel.WeatherService
import com.eslammongy.helper.weathermodel.dailyforecast.MyListDaily
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private val baseUrl = "http://api.openweathermap.org/"
    private val apiKey: String = "ca4f047ebf37d1d9d7dfebb4100bd981"
    private var requestPermissionCode: Int = 10
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var dailyForeCast: DailyForeCast
    var listOfDailyForecast = ArrayList<MyListDaily>()
    var forecastAdapter: ForecastAdapter? = null
    lateinit var lat: String
    lateinit var long: String
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

        dailyForeCast = DailyForeCast(activity!!)
        sharedPreferences = activity!!.getSharedPreferences("UserLocation", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        val lats = sharedPreferences.getString("UserLat", null)
        val lang = sharedPreferences.getString("UserLang", null)
        disableView()
        if (checkUserLocationPermission()){

            getCurrentWeatherDate(lats.toString(), lang.toString())
            getWeatherDaily(lats.toString(), lang.toString())

        }else{

            checkUserPermission(Manifest.permission.ACCESS_FINE_LOCATION , "Location" , requestPermissionCode)
            enableView()
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

    private fun enableView() {
        binding.circularProgressBar.visibility = View.GONE
        binding.parentView.alpha = 1.0F
    }
    private fun checkUserLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return false
        }

        return true
    }


    private fun checkUserPermission(permission: String, name: String, requestCode: Int) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    activity!!,
                    permission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(
                        activity!!,
                        "$name Permission Granted.",
                        Toast.LENGTH_LONG
                    ).show()

                    getCurrentLocation()

                }
                shouldShowRequestPermissionRationale(permission) -> {
                    showRequestPermissionDialog(permission, name, requestCode)
                }
                else -> {
                    ActivityCompat.requestPermissions(activity!!, arrayOf(permission), requestCode)

                }
            }
        }


    }

    private fun showRequestPermissionDialog(permissions: String, name: String, requestCode: Int) {

        val builder = AlertDialog.Builder(activity!!)
        builder.apply {
            setMessage("You need to access your $name permission is required to use this app")
            setTitle("Permission Required")
            setPositiveButton("Ok") { _, _ ->

                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(permissions),
                    requestCode
                )
            }
        }
        val dialog = builder.create()
        dialog.show()
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
                getCurrentLocation()
            }

    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        LocationServices.getFusedLocationProviderClient(activity!!)
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                @Override
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(activity!!)
                        .removeLocationUpdates(this)
                    if (locationResult.locations.size > 0) {
                        val locationIndex = locationResult.locations.size - 1
                        lat = locationResult.locations[locationIndex].latitude.toString()
                        long = locationResult.locations[locationIndex].longitude.toString()
                        editor.apply {
                            putString("UserLat", lat)
                            putString("UserLang", long)
                            apply()
                        }
                        getCurrentWeatherDate(lat,long)
                        getWeatherDaily(lat,long)
                    }
                }

            }, Looper.getMainLooper())

    }

    private fun getCurrentWeatherDate(lats: String, longs: String) {
        disableView()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val services = retrofit.create(WeatherService::class.java)
        val call = services.getCurrentWeatherStatus(lats, longs, apiKey, "metric")
        call.enqueue(object : Callback<WeatherResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<WeatherResponse>?, response: Response<WeatherResponse>?) {

                enableView()
                if (response!!.code() == 200) {
                    val weatherResponse = response.body()!!
                    binding.tvCityName.text =
                        weatherResponse.name + "${weatherResponse.sys!!.country}"
                    //Toast.makeText(activity,  "${weatherResponse.sys!!.country}", Toast.LENGTH_LONG).show()

                    val weatherStatusID = weatherResponse.weather[0].id
                    dailyForeCast.getWeatherDescriptionIconByID(
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

                }
            }

            override fun onFailure(call: Call<WeatherResponse>?, t: Throwable?) {

                Toast.makeText(activity, " Time Out ..  ${t!!.message}", Toast.LENGTH_LONG).show()
            }

        })


    }

    private fun getWeatherDaily(lats: String, longs: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val dailyServices = retrofit.create(WeatherService::class.java)
        val call = dailyServices.getDailyWeatherStatus(
            lats,
            longs,
            "hourly,minutely,current,alerts",
            apiKey,
            "metric"
        )
        call.enqueue(object : Callback<MyListDaily> {

            override fun onResponse(call: Call<MyListDaily>?, response: Response<MyListDaily>?) {

                if (response!!.code() == 200) {

                    val dailyResponse = response.body()
                    for (item in 0..6) {
                        listOfDailyForecast.add(dailyResponse!!)
                    }

                    binding.weatherRecyclerView.setHasFixedSize(true)
                    binding.weatherRecyclerView.hasFixedSize()
                    forecastAdapter = ForecastAdapter(activity!!, listOfDailyForecast)
                    binding.weatherRecyclerView.layoutManager =
                        LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
                    binding.weatherRecyclerView.adapter = forecastAdapter
                }

            }

            override fun onFailure(call: Call<MyListDaily>?, t: Throwable?) {
                Toast.makeText(activity, " .  ${t!!.message}", Toast.LENGTH_LONG).show()

            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}