package com.eslammongy.helper.onboardingSetup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.databinding.OnboardingItemContinarBinding

class OnboardingItemAdapter(private val listOnboardingItem: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingItemAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {

        return OnboardingViewHolder( OnboardingItemContinarBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(listOnboardingItem[position])
    }

    inner class OnboardingViewHolder(val binding: OnboardingItemContinarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val imageOnboarding = binding.viewPagerImage
        private var textTitle = binding.viewPagerText.text
        private var descText = binding.viewPagerDescText.text
        fun bind(onboardingItem: OnboardingItem) {

            imageOnboarding.setImageResource(onboardingItem.onboardingImage)
            textTitle = onboardingItem.titleText
            descText = onboardingItem.descriptionText
        }

    }

    override fun getItemCount(): Int {
        return listOnboardingItem.size
    }
}