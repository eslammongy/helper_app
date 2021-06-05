package com.eslammongy.helper.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.R
import com.eslammongy.helper.adapters.SubChlAdapter
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.SubCheckList
import com.eslammongy.helper.databinding.FragmentSubChlBinding
import com.eslammongy.helper.fragment.dialogs.ChlBottomSheet
import com.google.android.material.snackbar.Snackbar

class SubChlFragment (parentChlID:Int): Fragment() , View.OnClickListener {

    private var _binding:FragmentSubChlBinding? = null
    private val binding get() = _binding!!
    private var listOfSubChl = ArrayList<SubCheckList>()
    private var subChlAdapter: SubChlAdapter? = null
    private var parentChlID:Int = 0
    init {
        this.parentChlID = parentChlID
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
       _binding = FragmentSubChlBinding.inflate(inflater , container , false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displaySubCheckList()
        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    val position: Int = viewHolder.adapterPosition
                    val listChl: SubCheckList = listOfSubChl[position]
                    HelperDataBase.getDataBaseInstance(activity!!).checkListDao().deleteSelectedSubCheckList(listChl)
                    val deletedItem =
                        "Are You Sure You Want To Delete This " + listChl.subChl_Title + "OR Undo Deleted .."
                    listOfSubChl.removeAt(viewHolder.adapterPosition)
                    subChlAdapter!!.notifyDataSetChanged()
                    Snackbar.make(binding.subChlRecyclerView, deletedItem, Snackbar.LENGTH_LONG)
                        .setAction(
                            "Undo"
                        ) {

                            listOfSubChl.add(position, listChl)
                            HelperDataBase.getDataBaseInstance(activity!!).checkListDao().saveNewSubCheckList(listChl)
                            subChlAdapter!!.notifyItemInserted(position)

                        }.show()
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.subChlRecyclerView)
        binding.btnOpenSubChlSheet.setOnClickListener(this)
        binding.btnRefreshRecyclerView.setOnClickListener(this)
    }

    private fun displaySubCheckList(){
        binding.subChlRecyclerView.setHasFixedSize(true)
        binding.subChlRecyclerView.layoutManager = LinearLayoutManager(activity!!)
        subChlAdapter = SubChlAdapter(activity!!)
        binding.subChlRecyclerView.adapter = subChlAdapter!!
        listOfSubChl = HelperDataBase.getDataBaseInstance(activity!!).checkListDao().getAllSubCheckLists(parentChlID) as ArrayList<SubCheckList>
        subChlAdapter!!.setDate(listOfSubChl)

    }

    override fun onClick(v: View?) {

            when (v!!.id) {

                R.id.btnRefreshRecyclerView->{
                    listOfSubChl = HelperDataBase.getDataBaseInstance(activity!!).checkListDao().getAllSubCheckLists(parentChlID) as ArrayList<SubCheckList>
                    subChlAdapter!!.setDate(listOfSubChl)

                }
                R.id.btnOpenSubChlSheet -> {
                    if (parentChlID == 0){
                        Toast.makeText(activity!! , "You need to create parent checklist first then can do this." , Toast.LENGTH_LONG).show()
                    }else{
                        ChlBottomSheet(parentChlID).show(fragmentManager!!,"Tag")
                    }
                }
            }

    }

}