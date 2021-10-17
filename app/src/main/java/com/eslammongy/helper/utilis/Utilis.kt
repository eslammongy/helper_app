package com.eslammongy.helper.utilis

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.eslammongy.helper.R
import com.eslammongy.helper.ui.module.search.SearchScreen
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.muddzdev.styleabletoast.StyleableToast
import java.util.*


const val apiKey = "ca4f047ebf37d1d9d7dfebb4100bd981"

fun <A : Activity> Activity.startNewActivity( activity: Class<A> , intentID:Int) {
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        it.putExtra("ArrowKey" , intentID)
        startActivity(it)
    }
}

fun  Activity.startSearchActivity(SearchID:Int){
    Intent(this , SearchScreen::class.java).also {
        it.putExtra("SearchID" , SearchID)
        startActivity(it)
       this.finish()
    }
}

@SuppressLint("ResourceAsColor", "InflateParams")
fun showingSnackBar(view: View, message: String, color: String){

    val snackBar = Snackbar.make(view, message , Snackbar.LENGTH_SHORT)
    val layoutParams = CoordinatorLayout.LayoutParams(snackBar.view.layoutParams)
    snackBar.view.setBackgroundColor(Color.parseColor(color))
    val textView = snackBar.view.findViewById(R.id.snackbar_text) as TextView
    val textAction = snackBar.view.findViewById(R.id.snackbar_action) as TextView
    textAction.typeface = Typeface.DEFAULT_BOLD
    textAction.setTextColor(R.color.colorDark)
    textAction.textSize = 17f
    textView.text = message
    textView.setTextColor(Color.WHITE)
    textView.textSize = 18f
    layoutParams.gravity = Gravity.TOP
    layoutParams.setMargins(5 , 0 , 5 , 0)
    layoutParams.width = CoordinatorLayout.LayoutParams.MATCH_PARENT
    layoutParams.height = 250
    snackBar.duration = 3000
    snackBar.view.layoutParams = layoutParams
    snackBar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
    snackBar.setAction("Undo"){
        snackBar.dismiss()
    }

    snackBar.show()
}

 fun Activity.makePhoneCall(phoneNumber:String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    startActivity(intent)
}

 fun Activity.setToastMessage(message:String , color:Int){
     StyleableToast.Builder(this)
         .text(message)
         .textColor(Color.WHITE)
         .textBold()
         .gravity(Gravity.TOP)
         .cornerRadius(15)
         .backgroundColor(color)
         .textBold()
         .length(2000)
         .show()
}


@SuppressLint("CommitPrefEdits")
fun Activity.saveUserLatLang(latitude:String, longitude:String){
    val sharedPreferences = getSharedPreferences("UserLocation", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("latitude", latitude)
    editor.putString("longitude", longitude)
    editor.apply()

}

@SuppressLint("MissingPermission")
fun Activity.getCurrentLocation(callback: (Array<String>) -> Unit) {

    val locationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 3000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    LocationServices.getFusedLocationProviderClient(this)
        .requestLocationUpdates(locationRequest, object : LocationCallback() {
            @Override
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                LocationServices.getFusedLocationProviderClient(this@getCurrentLocation)
                    .removeLocationUpdates(this)
                if (locationResult.locations.size > 0) {
                    val locationIndex = locationResult.locations.size - 1
                   val latitude = locationResult.locations[locationIndex].latitude.toString()
                   val longitude = locationResult.locations[locationIndex].longitude.toString()
                    saveUserLatLang(latitude , longitude)
                     callback(arrayOf(latitude , longitude))
                    }
                }

        }
            , Looper.getMainLooper())

}

 fun Activity.getGreetingMessage(){

    val calender = Calendar.getInstance()
    val handEmo = "\uD83D\uDC4B"
    when (calender.get(Calendar.HOUR_OF_DAY)) {

        in 0..11 -> setToastMessage("Good Morning  $handEmo", Color.parseColor("#1AC231"))
        in 12..15 -> setToastMessage("Good Afternoon $handEmo", Color.parseColor("#1AC231"))
        in 16..20 -> setToastMessage("Good Evening $handEmo", Color.parseColor("#1AC231"))
        in 21..23 -> setToastMessage("Good Night $handEmo", Color.parseColor("#1AC231"))
        else -> Toast.makeText(this , "Hello" , Toast.LENGTH_LONG).show()
    }

}

fun notifyMessage():String{
    val calender = Calendar.getInstance()
   return when (calender.get(Calendar.HOUR_OF_DAY)) {

        in 0..11 -> "Good Morning"
        in 12..15 ->"Good Afternoon"
        in 16..20 ->"Good Evening"
        in 21..23 -> "Good Night"
        else -> "Hello"
    }
}
