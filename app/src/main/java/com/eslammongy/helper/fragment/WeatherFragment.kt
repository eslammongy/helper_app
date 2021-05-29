package com.eslammongy.helper.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eslammongy.helper.databinding.FragmentWeatherBinding

class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}