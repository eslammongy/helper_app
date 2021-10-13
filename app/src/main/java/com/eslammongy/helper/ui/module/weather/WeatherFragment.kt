package com.eslammongy.helper.ui.module.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.adapters.ForecastAdapter
import com.eslammongy.helper.databinding.FragmentWeatherBinding
import com.eslammongy.helper.utilis.*
import com.eslammongy.helper.ui.baseui.BaseFragment
import com.eslammongy.helper.model.MyListDaily
import com.eslammongy.helper.model.WeatherResponse
import com.eslammongy.helper.remoteApi.RetrofitBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class WeatherFragment : BaseFragment() {
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private val userLocation by lazy { UserPermission(requireActivity()) }
    private lateinit var sharedPreferences: SharedPreferences
    private  var latitude: String = ""
    private  var longitude: String = ""
    var listOfDailyForecast = ArrayList<MyListDaily>()
    private lateinit var forecastAdapter: ForecastAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(binding.root, savedInstanceState)

        disableView()
        forecastAdapter = ForecastAdapter(listOfDailyForecast)
        launch {
            sharedPreferences = requireActivity().getSharedPreferences("UserLocation", Context.MODE_PRIVATE)
            latitude = sharedPreferences.getString("latitude", "Noun").toString()
            longitude = sharedPreferences.getString("longitude", "Noun").toString()
        }
        if (userLocation.checkUserLocationPermission(Manifest.permission.ACCESS_FINE_LOCATION)){
            launch {

                    getCurrentWeatherDate(latitude , longitude)
                    getWeatherDaily(latitude , longitude)

            }
        }else{
            permReqLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }

    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value == true }
            if (granted) {
                requireActivity().setToastMessage("location permission granted" , Color.GREEN)
                disableView()
                    requireActivity().getCurrentLocation {
                        enableView("Success")
                        launch {
                            withContext(Dispatchers.IO) {
                                getCurrentWeatherDate(it[0] , it[1])
                                getWeatherDaily(it[0] , it[1])
                        }
                    }
                    }

            }else{
                enableView("Error")
                requireActivity().setToastMessage("location permission refused" , Color.RED)
            }
        }

    private fun disableView() {
        binding.circularProgressBar.visibility = View.VISIBLE
        binding.parentView.alpha = 0.3F
    }

    private fun enableView(error:String) {
        if (error == "Error"){
            binding.circularProgressBar.visibility = View.GONE
            binding.parentView.visibility = View.GONE
            binding.emptyImageView.visibility = View.VISIBLE
        }else{
            binding.parentView.visibility = View.VISIBLE
            binding.circularProgressBar.visibility = View.GONE
            binding.parentView.alpha = 1.0F
        }

    }
    private fun getCurrentWeatherDate(lats: String, longs: String) {

        RetrofitBuilder.ApiServices.getCurrentWeatherStatus(lats, longs, apiKey, "metric")
            .enqueue(object : Callback<WeatherResponse> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<WeatherResponse>?, response: Response<WeatherResponse>?) {

                        if (response!!.code() in 200..299){
                            enableView("Success")
                            val weatherResponse = response.body()!!
                            binding.tvCityName.text =
                                weatherResponse.name + "${weatherResponse.sys!!.country}"
                            val weatherStatusID = weatherResponse.weather[0].id
                            requireActivity().getWeatherDescriptionIconByID(weatherStatusID, binding.weatherStatusImage)
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
                        }else{
                            enableView("Error")
                         requireActivity().connectingError(requireView() , response.code())
                        }
                    }
                override fun onFailure(call: Call<WeatherResponse>?, t: Throwable?) {
                    enableView("Error")
                    requireActivity().showingSnackBar(
                        binding.root,
                        "Your Session has expired.",
                        "#DD2C00"
                    )
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

                if (response.code() in 200..299){
                    enableView("Success")
                        for (item in 0..7) {
                            listOfDailyForecast.add(dailyResponse!!)
                        }
                        displayDailyRecyclerView()
                    }else{
                 requireActivity().connectingError(requireView() , response.code())
                }
            }
            override fun onFailure(call: Call<MyListDaily>?, t: Throwable?) {
                //showingSnackBar(binding.root , "Your Session has expired." , "#DD2C00")
            }
        })

    }
    private fun displayDailyRecyclerView(){
        try {
            val recyclerLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
            binding.weatherRecyclerView.layoutManager = recyclerLayoutManager
            binding.weatherRecyclerView.setHasFixedSize(true)
            forecastAdapter = ForecastAdapter(listOfDailyForecast)
            binding.weatherRecyclerView.adapter = forecastAdapter

        }catch (e:IndexOutOfBoundsException){
            e.printStackTrace()
            requireActivity().showingSnackBar(binding.root, "${e.message}", "#DD2C00")
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}