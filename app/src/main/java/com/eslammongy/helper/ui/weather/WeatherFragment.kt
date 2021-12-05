package com.eslammongy.helper.ui.weather

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.adapters.ForecastAdapter
import com.eslammongy.helper.databinding.FragmentWeatherBinding
import com.eslammongy.helper.ui.baseui.BaseFragment
import com.eslammongy.helper.utilis.*
import com.eslammongy.helper.viewModels.WeatherViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.cancel
import java.lang.ClassCastException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class WeatherFragment : BaseFragment() {
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private val userLocation by lazy { UserPermission(requireActivity()) }
    private lateinit var sharedPreferences: SharedPreferences
    private var latitude: String = ""
    private var longitude: String = ""
    private lateinit var weatherViewModel: WeatherViewModel
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
        super.onViewCreated(view, savedInstanceState)

        weatherViewModel = ViewModelProvider(requireActivity(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(WeatherViewModel::class.java)

        sharedPreferences = requireActivity().getSharedPreferences("UserLocation", Context.MODE_PRIVATE)
        disableView()
        if (userLocation.checkUserLocationPermission(Manifest.permission.ACCESS_FINE_LOCATION)){
            latitude = sharedPreferences.getString("latitude", "Noun").toString()
            longitude = sharedPreferences.getString("longitude", "Noun").toString()
            if (CheckInternetConnection.checkForInternet(requireActivity())){
                getCurrentWeatherDate(latitude, longitude)
                getWeatherDaily(latitude, longitude)
            }else{
                enableView("Error")
            }

        } else {
            permReqLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }

    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value == true }
            if (granted) {
                if (checkLocationEnable(requireContext())){
                    requireActivity().getCurrentLocation {
                        getWeatherDaily(it[0], it[1])
                        getCurrentWeatherDate(it[0], it[1])
                    }
                }else{
                    enableLocationDialog(requireContext())

                }
            } else {
                enableView("Error")
                requireActivity().setToastMessage("Permission Refused", Color.parseColor("#CB0003"))
            }
        }

    private fun enableLocationDialog(context: Context) {

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        var response: LocationSettingsResponse? = null
        val result: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(context).checkLocationSettings(builder.build())
        result.addOnCompleteListener{task ->
            try {
                response = task.getResult(ApiException::class.java)
               // Toast.makeText(context , "1" , Toast.LENGTH_LONG).show()
            }catch (exception: ApiException){
                when(exception.statusCode){
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->{
                        try {
                            //Toast.makeText(context , "2" , Toast.LENGTH_LONG).show()
                            val reSolveException: ResolvableApiException = exception as ResolvableApiException
                            reSolveException.startResolutionForResult(context as Activity, LocationRequest.PRIORITY_HIGH_ACCURACY)
                            requireActivity().getCurrentLocation {
                                getWeatherDaily(it[0], it[1])
                                getCurrentWeatherDate(it[0], it[1])
                            }
                        }catch (ex: IntentSender.SendIntentException){
                          //  Toast.makeText(context , "3" , Toast.LENGTH_LONG).show()
                            Log.e("TAG" , ex.message!!)
                        }catch (ex: ClassCastException){
                            Toast.makeText(context , "4" , Toast.LENGTH_LONG).show()
                            Log.e("TAG" , ex.message!!)
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->{
                        Log.e("TAG" , "UNAVAILABLE")
                    }
                }
            }
        }

    }

    private fun disableView() {
        binding.circularProgressBar.visibility = View.VISIBLE
        binding.parentView.alpha = 0.3F
    }

    private fun enableView(error: String) {
        binding.circularProgressBar.visibility = View.GONE
        binding.parentView.alpha = 1.0F
        if (error == "Error") {
            binding.emptyImageView.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getCurrentWeatherDate(lats: String, longs: String) {

        weatherViewModel.getCurrentWeatherState(lats, longs, requireView())
        weatherViewModel.currentWeatherList.observe(viewLifecycleOwner, { response ->

            if (response.equals(null)) {
                enableView("Error")
            } else {
                enableView("Success")
                binding.tvCityName.text = response.name + "${response.sys!!.country}"
                val weatherStatusID = response.weather[0].id
                requireActivity().getWeatherDescriptionIconByID(weatherStatusID, binding.weatherStatusImage)
                val weatherStatusDec = response.weather[0].description
                binding.tvWeatherStatus.text = weatherStatusDec.toString()
                val updatedAt = response.dt.toLong()
                val updatedAtText = "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt * 1000))
                binding.tvLastUpdate.text = updatedAtText
                val currentTemp = response.main!!.temp.roundToInt()
                binding.tvWeatherTempDegree.text = "${currentTemp}°C"
                val minTemp = response.main!!.temp_min.roundToInt()
                binding.tvMinTemp.text = "Min $minTemp ºC"
                val maxTemp = response.main!!.temp_max.roundToInt()
                binding.tvMaxTemp.text = "Max $maxTemp ºC"
            }
        })

    }

    private fun getWeatherDaily(lats: String, longs: String) {

        val recyclerLayoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
        binding.weatherRecyclerView.layoutManager = recyclerLayoutManager
        binding.weatherRecyclerView.setHasFixedSize(true)
        forecastAdapter = ForecastAdapter()
        binding.weatherRecyclerView.adapter = forecastAdapter
        weatherViewModel.getDialWeather(lats, longs)
        weatherViewModel.dailyWeatherList.observe(viewLifecycleOwner, { list ->
            if (list.isNullOrEmpty()) {
                enableView("Error")
            } else {
                enableView("Success")
                forecastAdapter.differ.submitList(list)
            }

        })

    }

    override fun onDestroy() {
        super.onDestroy()
        weatherViewModel.viewModelScope.cancel("Close")
        _binding = null
    }

}