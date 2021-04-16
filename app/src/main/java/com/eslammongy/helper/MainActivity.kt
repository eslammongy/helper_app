package com.eslammongy.helper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.content.withStyledAttributes

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private val splashTimeOut = 3000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler().postDelayed(
                {

                val intent = Intent(this , OnboardingActivity::class.java)
                    startActivity(intent)
                    finish()

                }, splashTimeOut)

    }
}