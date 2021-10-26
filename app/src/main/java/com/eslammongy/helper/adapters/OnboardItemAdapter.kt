package com.eslammongy.helper.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.databinding.OnboardingItemContinarBinding
import com.eslammongy.helper.ui.onboardingSetup.OnBoardingItem

class OnboardItemAdapter(private var context:Context, private val listOnBoardingItem: ArrayList<OnBoardingItem>) :
    RecyclerView.Adapter<OnboardItemAdapter.OnboardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardViewHolder {

        return OnboardViewHolder( OnboardingItemContinarBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: OnboardViewHolder, position: Int) {
         val onboardItem = listOnBoardingItem[position]
        holder.binding.viewPagerImage.setImageResource(onboardItem.onBoardingImage)
        holder.binding.viewPagerText.text = onboardItem.titleText
        holder.binding.viewPagerDescText.text = onboardItem.descriptionText

    }

    inner class OnboardViewHolder(val binding: OnboardingItemContinarBinding) : RecyclerView.ViewHolder(binding.root)
    override fun getItemCount(): Int {
        return listOnBoardingItem.size
    }
}