package com.eslammongy.helper.ui.sublist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.adapters.SubChlAdapter
import com.eslammongy.helper.data.entities.SubCheckList
import com.eslammongy.helper.databinding.FragmentSubChlBinding
import com.eslammongy.helper.ui.baseui.BaseFragment
import com.eslammongy.helper.viewModels.ChListViewModel
import com.google.android.material.snackbar.Snackbar

class SubChlFragment(parentChlID: Int , parentChlTitle:String ) : BaseFragment() {

    private var _binding: FragmentSubChlBinding? = null
    private val binding get() = _binding!!
    private lateinit var chListViewModel: ChListViewModel

    private lateinit var subChlAdapter: SubChlAdapter
    private var parentChlID: Int = 0
    private var parentChlTitle:String? = null

    init {
        this.parentChlID = parentChlID
        this.parentChlTitle = parentChlTitle
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubChlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chListViewModel = ViewModelProvider(this , ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))
            .get(ChListViewModel::class.java)

        displaySubCheckList(parentChlID )

        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
                    ItemTouchHelper.RIGHT.or(
                        ItemTouchHelper.LEFT)
                ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    val startPosition = viewHolder.adapterPosition
                    val endPosition = target.adapterPosition
                    subChlAdapter.moveItem(startPosition , endPosition)
                    //binding.subChlRecyclerView.adapter!!.notifyItemMoved(startPosition, endPosition)

                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    val position: Int = viewHolder.adapterPosition
                    val listChl: SubCheckList = subChlAdapter.differ.currentList[position]
                    val listSize = subChlAdapter.differ.currentList.size
                    val deletedItem =
                        "Are you sure you want to delete this " + listChl.subChl_Title + "or undo deleted it .."

                    chListViewModel.deleteCurrentSubChList(listChl)
                    subChlAdapter.notifyDataSetChanged()
                    Snackbar.make(binding.subChlRecyclerView, deletedItem, Snackbar.LENGTH_LONG)
                        .setAction(
                            "Undo"
                        ) {
                            chListViewModel.saveNewSubChList(listChl)
                            subChlAdapter.notifyItemRangeInserted(listSize , subChlAdapter.differ.currentList.size-1)
                        }.show()
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.subChlRecyclerView)

    }

    private  fun displaySubCheckList(parentChlID:Int) {
        subChlAdapter = SubChlAdapter(requireActivity())
        binding.subChlRecyclerView.apply {
            binding.subChlRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.subChlRecyclerView.setHasFixedSize(true)
            adapter = subChlAdapter
        }
        chListViewModel.getAllSubChList(parentChlID).observe(viewLifecycleOwner , {
            if (it.isEmpty()){
                binding.emptyImageView.visibility = View.VISIBLE
                subChlAdapter.differ.submitList(it)
            }else{
                binding.emptyImageView.visibility = View.GONE
                subChlAdapter.differ.submitList(it)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
