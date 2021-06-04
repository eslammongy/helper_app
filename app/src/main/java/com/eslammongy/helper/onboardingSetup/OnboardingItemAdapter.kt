package com.eslammongy.helper.onboardingSetup

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.databinding.OnboardingItemContinarBinding

class OnboardingItemAdapter(private var context:Context ,private val listOnboardingItem: ArrayList<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingItemAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {

        return OnboardingViewHolder( OnboardingItemContinarBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
         val onboardingItem = listOnboardingItem[position]
        holder.binding.viewPagerImage.setImageResource(onboardingItem.onboardingImage)
        holder.binding.viewPagerText.text = onboardingItem.titleText
        holder.binding.viewPagerDescText.text = onboardingItem.descriptionText

    }

    inner class OnboardingViewHolder(val binding: OnboardingItemContinarBinding) : RecyclerView.ViewHolder(binding.root)
    override fun getItemCount(): Int {
        return listOnboardingItem.size
    }
}