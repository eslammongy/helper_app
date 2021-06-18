package com.eslammongy.helper.helperfun

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices


@SuppressLint("MissingPermission")
class UserPermission(var activity: Activity?) {

    private lateinit var editor: SharedPreferences.Editor
    private val galleryPermissionCode = 101
    private val pickAndCropImage by lazy { PickAndCropImage(activity!!, galleryPermissionCode) }
     var lat: String? = null
     var long: String? = null


    fun checkUserLocationPermission(): Boolean {
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

    fun checkUserPermission(permission: String, name: String, requestCode: Int){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    activity!!, permission) == PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(activity!!, "$name Permission Granted.", Toast.LENGTH_LONG).show()
                    if (name == "Location" && requestCode == 10) {
                        getCurrentLocation()
                        Toast.makeText(activity, "$lat ... $long inside if checkPermission", Toast.LENGTH_LONG).show()
                    } else {
                        pickAndCropImage.pickImageFromGallery()
                    }
                }
                activity!!.shouldShowRequestPermissionRationale(permission) -> {
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

    @SuppressLint("MissingPermission", "CommitPrefEdits")
    fun getCurrentLocation() {
        val sharedPreferences = activity!!.getSharedPreferences("UserLocation", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
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
                            Toast.makeText(activity, "$lat ... $long inside if getCurrentLocation", Toast.LENGTH_LONG).show()
                            apply()
                        }

                    }
                }

            }, Looper.getMainLooper())

    }

}
