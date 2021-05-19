package com.eslammongy.helper

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.eslammongy.helper.ui.HomeActivity
import com.eslammongy.helper.ui.OnboardingActivity

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private val splashTimeOut = 3000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val isFirstRun = getSharedPreferences("SharedPreference" , Context.MODE_PRIVATE)
            .getBoolean("isFirstRun" , true)

        if (isFirstRun){
            Handler().postDelayed(
                {
                    val intent = Intent(this , OnboardingActivity::class.java)
                    intent.putExtra("ToastMessage" , 101)
                    startActivity(intent)
                    finish()

                }, splashTimeOut)
        }else{
            Handler().postDelayed(
                {
                    val intent = Intent(this , HomeActivity::class.java)
                    intent.putExtra("ToastMessage" , 101)
                    startActivity(intent)
                    finish()

                }, splashTimeOut)
        }

        getSharedPreferences("SharedPreference" , Context.MODE_PRIVATE).edit()
            .putBoolean("isFirstRun" , false).apply()


    }
}