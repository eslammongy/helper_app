package com.eslammongy.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.eslammongy.helper.ui.module.home.HomeScreen
import com.eslammongy.helper.ui.module.onboardingSetup.OnBoardingScreen
import com.eslammongy.helper.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityMainBinding
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var job: Job
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        job = Job()

        val isFirstRun = getSharedPreferences("SharedPreference", Context.MODE_PRIVATE).getBoolean("isFirstRun", true)

            if (isFirstRun) {
                launch {
                    startNewScreen(WeakReference<Activity>(this@MainActivity), OnBoardingScreen())
                }
            } else {
                launch {
                    startNewScreen(WeakReference<Activity>(this@MainActivity), HomeScreen())

                }
            }

            getSharedPreferences("SharedPreference", Context.MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).apply()
        }

        private fun startNewScreen(weakActivity: WeakReference<Activity>, toActivity: Activity) {
            val myActivity = weakActivity.get()
            myActivity?.let {
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(weakActivity.get(), toActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("ToastMessage" , 101)
                    startActivity(intent)
                    finish()
                }, 2000)

            }
        }

        override fun onStop() {
            job.cancel()
            finish()
            super.onStop()
        }

        override fun onDestroy() {
            job.cancel()
            finish()
            super.onDestroy()

        }

    }