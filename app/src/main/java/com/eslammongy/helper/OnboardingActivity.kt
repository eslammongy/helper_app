package com.eslammongy.helper

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.RecoverySystem
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.eslammongy.helper.onboardingSetup.OnboardingItem
import com.eslammongy.helper.onboardingSetup.OnboardingItemAdapter

class OnboardingActivity : AppCompatActivity() {

    private lateinit var onboardingItemsAdapter: OnboardingItemAdapter
    private lateinit var pointContainer: LinearLayout
    private lateinit var onboardingViewPager:ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        setOnboardingItemsAdapter()
        setPointContainer()
        setCurrentPint(0)
    }

    private fun setOnboardingItemsAdapter() {

        onboardingItemsAdapter = OnboardingItemAdapter(listOf(
                OnboardingItem(R.drawable.manage_your_task_image, "Manage Your Task",
                        "Organize all your do,s and tasks. Color tag them to set priorities and categories."),
                OnboardingItem(R.drawable.work_on_time_image, "Work On Time",
                        "When you are overwhelmed by the amount of the work you have on your plate , stop and rethink."),
                OnboardingItem(R.drawable.reminder_me_task_image, "Get Reminder On Time",
                        "when you encounter a small task that takes less than 5 minutes to complete.")
        ))

        onboardingViewPager = findViewById<ViewPager2>(R.id.onBoardingViewPager2)
        onboardingViewPager.adapter = onboardingItemsAdapter

        onboardingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentPint(position)
            }
        })

        (onboardingViewPager.getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

    }

    fun swipeToHome(view:View){

        if (onboardingViewPager.currentItem +1 < onboardingItemsAdapter.itemCount){
            onboardingViewPager.currentItem += 1

        }else{

            val intent = Intent(this , HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setPointContainer() {

        pointContainer = findViewById(R.id.pointLinearLayout)
        val points = arrayOfNulls<ImageView>(onboardingItemsAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(10, 0, 10, 0)
        for (i in points.indices) {

            points[i] = ImageView(applicationContext)
            points[i]?.let {
                it.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.point_inactive_background))
                it.layoutParams = layoutParams
                pointContainer.addView(it)
            }
        }
    }

    private fun setCurrentPint(position:Int){

        val childCount = pointContainer.childCount

        for (i in 0 until childCount){

            val imageView = pointContainer.getChildAt(i) as ImageView
            if (i == position){
                imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext , R.drawable.point_active_background))
            }else{

                imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext , R.drawable.point_inactive_background))
            }
        }
    }
}