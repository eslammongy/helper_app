package com.eslammongy.helper.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eslammongy.helper.R
import com.eslammongy.helper.databinding.FragmentCheckListBinding

class CheckListFragment : Fragment() {

    private var _binding:FragmentCheckListBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =FragmentCheckListBinding.inflate( inflater , container, false)


        return binding.root
    }

}