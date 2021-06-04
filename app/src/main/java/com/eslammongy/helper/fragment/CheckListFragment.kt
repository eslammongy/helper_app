package com.eslammongy.helper.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.eslammongy.helper.adapters.CheckListAdapter
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.CheckListEntity
import com.eslammongy.helper.databinding.FragmentCheckListBinding

class CheckListFragment : Fragment() {

    private var _binding:FragmentCheckListBinding? = null
    private val binding get() = _binding!!
    private var checkListAdapter:CheckListAdapter? = null
    private var listOfCheckList = ArrayList<CheckListEntity>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =FragmentCheckListBinding.inflate( inflater , container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listOfCheckList = HelperDataBase.getDataBaseInstance(activity!!).checkListDao().getAllCheckLists() as ArrayList<CheckListEntity>
        checkListAdapter = CheckListAdapter(activity!! , listOfCheckList)
        binding.chlRecyclerView.setHasFixedSize(true)
        binding.chlRecyclerView.layoutManager = LinearLayoutManager(activity!!)
        binding.chlRecyclerView.adapter = checkListAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}