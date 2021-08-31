package com.eslammongy.helper.ui.module.checklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.R
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.CheckListEntity
import com.eslammongy.helper.databinding.FragmentCheckListBinding
import com.eslammongy.helper.utilis.startNewActivity
import com.eslammongy.helper.utilis.startSearchActivity
import com.eslammongy.helper.ui.baseui.BaseFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class CheckListFragment : BaseFragment(), View.OnClickListener {
    private var _binding: FragmentCheckListBinding? = null
    private val binding get() = _binding!!
    private val checkListAdapter by lazy {  CheckListAdapter(requireContext())}
    private var listOfCheckList = ArrayList<CheckListEntity>()
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
        launch {
            displayRecyclerView()
        }

        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    val startPosition = viewHolder.adapterPosition
                    val endPosition = target.adapterPosition
                    Collections.swap(listOfCheckList, startPosition, endPosition)
                    binding.chlRecyclerView.adapter!!.notifyItemMoved(startPosition, endPosition)

                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    val position: Int = viewHolder.adapterPosition
                    val listChl:CheckListEntity = listOfCheckList[position]
                    launch {
                        HelperDataBase.getDataBaseInstance(requireActivity()).checkListDao().deleteSelectedCheckList(listChl)
                    }
                    val deletedItem =
                        "Are You Sure You Want To Delete This " + listChl.checkList_Title + "OR Undo Deleted .."
                    listOfCheckList.removeAt(viewHolder.adapterPosition)
                    checkListAdapter.notifyDataSetChanged()
                    Snackbar.make(binding.chlRecyclerView, deletedItem, Snackbar.LENGTH_LONG)
                        .setAction(
                            "Undo"
                        ) {
                            listOfCheckList.add(position, listChl)
                            launch {
                                HelperDataBase.getDataBaseInstance(requireActivity()).checkListDao().saveNewCheckList(listChl)
                            }
                            checkListAdapter.notifyItemInserted(position)

                        }.show()
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.chlRecyclerView)
    }

    private suspend fun displayRecyclerView(){
        listOfCheckList = HelperDataBase.getDataBaseInstance(requireActivity()).checkListDao().getAllCheckLists() as ArrayList<CheckListEntity>
        if (listOfCheckList.isNullOrEmpty()){
            binding.emptyImageView.visibility = View.VISIBLE
        }else{
            binding.emptyImageView.visibility = View.GONE
            binding.chlRecyclerView.setHasFixedSize(true)
            binding.chlRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
            binding.chlRecyclerView.adapter = checkListAdapter
            checkListAdapter.setData(listOfCheckList)

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_AddNewChl -> {
               requireActivity().startNewActivity(AddNewCheckList::class.java , 2)
            }
            R.id.btn_SearchChl ->{
                requireActivity().startSearchActivity(2)
            }
        }
    }
}