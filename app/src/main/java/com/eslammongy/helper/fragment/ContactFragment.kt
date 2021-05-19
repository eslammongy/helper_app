package com.eslammongy.helper.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eslammongy.helper.R
import com.eslammongy.helper.databinding.FragmentCheckListBinding
import com.eslammongy.helper.databinding.FragmentContactBinding

class ContactFragment : Fragment() {
    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =FragmentContactBinding.inflate(inflater, container, false)

        return binding.root
    }

}