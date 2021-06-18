package com.eslammongy.helper.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.R
import com.eslammongy.helper.adapters.CheckListAdapter
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.CheckListEntity
import com.eslammongy.helper.databinding.FragmentCheckListBinding
import com.eslammongy.helper.fragment.dialogs.CustomDeleteDialog
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.collections.ArrayList

class CheckListFragment : Fragment(), CheckListAdapter.OnItemSelectedListener {

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

        displayRecyclerView()
        binding.reFreshRecyclerView.setOnRefreshListener {
            binding.reFreshRecyclerView.isRefreshing = false
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
                    HelperDataBase.getDataBaseInstance(requireActivity()).checkListDao().deleteSelectedCheckList(listChl)
                    val deletedItem =
                        "Are You Sure You Want To Delete This " + listChl.checkList_Title + "OR Undo Deleted .."
                    listOfCheckList.removeAt(viewHolder.adapterPosition)
                    checkListAdapter!!.notifyDataSetChanged()
                    Snackbar.make(binding.chlRecyclerView, deletedItem, Snackbar.LENGTH_LONG)
                        .setAction(
                            "Undo"
                        ) {

                            listOfCheckList.add(position, listChl)
                            HelperDataBase.getDataBaseInstance(requireActivity()).checkListDao().saveNewCheckList(listChl)
                            checkListAdapter!!.notifyItemInserted(position)

                        }.show()
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.chlRecyclerView)
    }

    private fun displayRecyclerView(){
        listOfCheckList = HelperDataBase.getDataBaseInstance(requireActivity()).checkListDao().getAllCheckLists() as ArrayList<CheckListEntity>

        if (listOfCheckList.isNullOrEmpty()){
                binding.emptyImageView.visibility = View.VISIBLE
            }else{
            binding.emptyImageView.visibility = View.GONE
            binding.chlRecyclerView.setHasFixedSize(true)
            binding.chlRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
            checkListAdapter = CheckListAdapter(requireActivity())
            binding.chlRecyclerView.adapter = checkListAdapter
            checkListAdapter!!.setData(listOfCheckList)

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClicked(checkListEntity: CheckListEntity, position: Int) {
        val dialogFragment = CustomDeleteDialog(checkListEntity.checkListId , 3)
        openFrameLayout(dialogFragment)
    }

    private fun openFrameLayout(fragment: Fragment) {

        val transaction = parentFragmentManager.beginTransaction()
        transaction.add(R.id.chlFragmentContainer, fragment)
        transaction.commit()

    }
}