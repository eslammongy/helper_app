package com.eslammongy.helper.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.eslammongy.helper.R
import com.eslammongy.helper.databinding.ActivityOnboardingBinding
import com.eslammongy.helper.onboardingSetup.OnboardingItem
import com.eslammongy.helper.onboardingSetup.OnboardingItemAdapter

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingItemsAdapter: OnboardingItemAdapter
    private val listOnBoardingItem = ArrayList<OnboardingItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnboardingItemsAdapter()
        setPointContainer()
        setCurrentPint(0)
    }

    private fun setOnboardingItemsAdapter() {
        listOnBoardingItem.add(OnboardingItem(
            R.drawable.manage_your_task_image, "Manage Your Task",
            "Organize all your do,s and tasks. Color tag them to set priorities and categories."))
        listOnBoardingItem.add( OnboardingItem(
            R.drawable.work_on_time_image, "Work On Time",
            "When you are overwhelmed by the amount of the work you have on your plate , stop and rethink."))
        listOnBoardingItem.add(OnboardingItem(
            R.drawable.reminder_me_task_image, "Get Reminder On Time",
            "when you encounter a small task that takes less than 5 minutes to complete."))

        onboardingItemsAdapter = OnboardingItemAdapter(this , listOnBoardingItem)

        binding.onBoardingViewPager2.adapter = onboardingItemsAdapter

        binding.onBoardingViewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentPint(position)
            }
        })

        ( binding.onBoardingViewPager2.getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

    }

    fun swipeToHome(view:View){

        if ( binding.onBoardingViewPager2.currentItem +1 < onboardingItemsAdapter.itemCount){
            binding.onBoardingViewPager2.currentItem += 1

        }else{

            val intent = Intent(this , HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setPointContainer() {

        val points = arrayOfNulls<ImageView>(onboardingItemsAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(10, 0, 10, 0)
        for (i in points.indices) {

            points[i] = ImageView(applicationContext)
            points[i]?.let {
                it.setImageDrawable(ContextCompat.getDrawable(applicationContext,
                    R.drawable.point_inactive_background
                ))
                it.layoutParams = layoutParams
                binding.pointLinearLayout.addView(it)
            }
        }
    }

    private fun setCurrentPint(position:Int){

        val childCount = binding.pointLinearLayout.childCount

        for (i in 0 until childCount){

            val imageView = binding.pointLinearLayout.getChildAt(i) as ImageView
            if (i == position){
                imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext ,
                    R.drawable.point_active_background
                ))
            }else{

                imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext ,
                    R.drawable.point_inactive_background
                ))
            }
        }
    }


}