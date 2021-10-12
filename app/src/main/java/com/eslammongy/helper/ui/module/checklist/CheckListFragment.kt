package com.eslammongy.helper.ui.module.checklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.R
import com.eslammongy.helper.adapters.CheckListAdapter
import com.eslammongy.helper.database.entities.CheckListEntity
import com.eslammongy.helper.databinding.FragmentCheckListBinding
import com.eslammongy.helper.utilis.startNewActivity
import com.eslammongy.helper.utilis.startSearchActivity
import com.eslammongy.helper.viewModels.ChListViewModel
import com.google.android.material.snackbar.Snackbar

class CheckListFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentCheckListBinding? = null
    private val binding get() = _binding!!
    private lateinit var checkListAdapter:CheckListAdapter
    private lateinit var chListViewModel: ChListViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAddNewChl.setOnClickListener(this)
        binding.btnSearchChl.setOnClickListener(this)
        chListViewModel = ViewModelProvider(this , ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))
            .get(ChListViewModel::class.java)
        displayRecyclerView()
        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    val startPosition = viewHolder.adapterPosition
                    val endPosition = target.adapterPosition
                   // Collections.swap(checkListAdapter.differ.currentList, startPosition, endPosition)
                    binding.chlRecyclerView.adapter!!.notifyItemMoved(startPosition, endPosition)

                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    val position: Int = viewHolder.adapterPosition
                    val listChl: CheckListEntity = checkListAdapter.differ.currentList[position]

                    val deletedItem =
                        "Are You Sure You Want To Delete This " + listChl.checkList_Title + "OR Undo Deleted .."
                    //checkListAdapter.differ.currentList.removeAt(viewHolder.adapterPosition)
                    chListViewModel.deleteCurrentChLIst(listChl)
                    checkListAdapter.notifyDataSetChanged()
                    Snackbar.make(binding.chlRecyclerView, deletedItem, Snackbar.LENGTH_LONG)
                        .setAction(
                            "Undo"
                        ) {
                            chListViewModel.saveNewChLIst(listChl)
                            checkListAdapter.notifyItemInserted(position)

                        }.show()
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.chlRecyclerView)
    }

    private fun displayRecyclerView() {
        checkListAdapter = CheckListAdapter(requireContext())
        binding.chlRecyclerView.apply {
            binding.chlRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.chlRecyclerView.setHasFixedSize(true)
            adapter = checkListAdapter
        }
        chListViewModel.allCheckLists.observe(viewLifecycleOwner , {
            if (it.isEmpty()){
                binding.emptyImageView.visibility = View.VISIBLE
                checkListAdapter.differ.submitList(it)
            }else{
                binding.emptyImageView.visibility = View.GONE
                checkListAdapter.differ.submitList(it)
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_AddNewChl -> {
                requireActivity().startNewActivity(AddNewCheckList::class.java, 2)
            }
            R.id.btn_SearchChl -> {
                requireActivity().startSearchActivity(2)
            }
        }
    }
}