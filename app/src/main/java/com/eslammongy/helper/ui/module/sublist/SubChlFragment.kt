package com.eslammongy.helper.ui.module.sublist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.adapters.SubChlAdapter
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.SubCheckList
import com.eslammongy.helper.databinding.FragmentSubChlBinding
import com.eslammongy.helper.ui.baseui.BaseFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.*

class SubChlFragment(parentChlID: Int , parentChlTitle:String ) : BaseFragment() {

    private var _binding: FragmentSubChlBinding? = null
    private val binding get() = _binding!!
    private  var listOfSubChl = ArrayList<SubCheckList>()
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

        launch {
            displaySubCheckList(parentChlID )
        }
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
                    Collections.swap(listOfSubChl, startPosition, endPosition)
                    binding.subChlRecyclerView.adapter!!.notifyItemMoved(startPosition, endPosition)

                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    val position: Int = viewHolder.adapterPosition
                    val listChl: SubCheckList = subChlAdapter.differ.currentList[position]
                    launch {
                        HelperDataBase.getDataBaseInstance(activity!!).checkListDao()
                            .deleteSelectedSubCheckList(listChl)
                    }
                    val deletedItem =
                        "Are you sure you want to delete this " + listChl.subChl_Title + "or undo deleted it .."
                    listOfSubChl.removeAt(viewHolder.adapterPosition)

                    subChlAdapter.notifyDataSetChanged()
                    Snackbar.make(binding.subChlRecyclerView, deletedItem, Snackbar.LENGTH_LONG)
                        .setAction(
                            "Undo"
                        ) {

                            listOfSubChl.add(position, listChl)
                            launch {
                                HelperDataBase.getDataBaseInstance(activity!!).checkListDao().saveNewSubCheckList(listChl)
                            }
                            subChlAdapter.notifyItemInserted(position)
                        }.show()
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.subChlRecyclerView)

        binding.btnRefreshRecyclerView.setOnClickListener {
            launch {
                displaySubCheckList(parentChlID)
            }
        }
    }

    private suspend fun displaySubCheckList(parentChlID:Int) {
        subChlAdapter = SubChlAdapter(requireActivity())
        binding.subChlRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.subChlRecyclerView.setHasFixedSize(true)
        listOfSubChl = HelperDataBase.getDataBaseInstance(requireContext()).checkListDao().getAllSubCheckLists(parentChlID) as ArrayList<SubCheckList>
        if (listOfSubChl.isEmpty()){
            binding.emptyImageView.visibility = View.VISIBLE
        }else {
            binding.emptyImageView.visibility = View.GONE
            binding.subChlRecyclerView.adapter = subChlAdapter
            subChlAdapter.differ.submitList(listOfSubChl)
       }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        launch {
            displaySubCheckList(parentChlID)
        }
    }

}
