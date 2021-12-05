package com.eslammongy.helper.ui.onboardingSetup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.eslammongy.helper.R
import com.eslammongy.helper.adapters.OnboardItemAdapter
import com.eslammongy.helper.ui.home.HomeScreen
import com.eslammongy.helper.databinding.ActivityOnBoadrdinScreenBinding

class OnBoardingScreen : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityOnBoadrdinScreenBinding
    private lateinit var onboardItemsAdapter: OnboardItemAdapter
    private val listOnBoardingItem = ArrayList<OnBoardingItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoadrdinScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnboardItemsAdapter()
        binding.btnSwipeToHome.setOnClickListener(this)
        setPointContainer()
        setCurrentPint(0)
    }

    private fun setOnboardItemsAdapter() {
        listOnBoardingItem.add(
            OnBoardingItem(
            R.drawable.manage_your_task_image, "Manage Your Task",
            "Organize all your do,s and tasks. Color tag them to set priorities and categories.")
        )
        listOnBoardingItem.add( OnBoardingItem(
            R.drawable.work_on_time_image, "Work On Time",
            "When you are overwhelmed by the amount of the work you have on your plate , stop and rethink."))
        listOnBoardingItem.add(OnBoardingItem(
            R.drawable.reminder_me_task_image, "Get Reminder On Time",
            "when you encounter a small task that takes less than 5 minutes to complete."))

        onboardItemsAdapter = OnboardItemAdapter(this , listOnBoardingItem)

        binding.onBoardingViewPager2.adapter = onboardItemsAdapter

        binding.onBoardingViewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentPint(position)
            }
        })

        ( binding.onBoardingViewPager2.getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

    }

    private fun swipeToHome(){

        if ( binding.onBoardingViewPager2.currentItem +1 < onboardItemsAdapter.itemCount){
            binding.onBoardingViewPager2.currentItem += 1

        }else{

            val intent = Intent(this , HomeScreen::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setPointContainer() {

        val points = arrayOfNulls<ImageView>(onboardItemsAdapter.itemCount)
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
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext ,
                    R.drawable.point_active_background
                ))
            }else{

                imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext ,
                    R.drawable.point_inactive_background
                ))
            }
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_SwipeToHome ->{
                swipeToHome()
            }
        }
    }

}