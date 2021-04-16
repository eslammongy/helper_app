package com.eslammongy.helper.onboardingSetup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.R

class OnboardingItemAdapter(private val listOnboardingItem: List<OnboardingItem>) :
        RecyclerView.Adapter<OnboardingItemAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imageOnboarding = view.findViewById<ImageView>(R.id.viewPagerImage)
        private val textTitle = view.findViewById<TextView>(R.id.viewPagerText)
        private val descText = view.findViewById<TextView>(R.id.viewPagerDescText)

        fun bind(onboardingItem: OnboardingItem) {

            imageOnboarding.setImageResource(onboardingItem.onboardingImage)
            textTitle.text = onboardingItem.titleText
            descText.text = onboardingItem.descriptionText
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {

        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.onboarding_item_continar , parent , false)
        return OnboardingViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(listOnboardingItem[position])
    }

    override fun getItemCount(): Int {
        return listOnboardingItem.size
    }
}