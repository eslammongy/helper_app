package com.eslammongy.helper.ui.module.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.eslammongy.helper.R
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.databinding.FragmentContactsBinding
import com.eslammongy.helper.utilis.startNewActivity
import com.eslammongy.helper.utilis.startSearchActivity
import com.eslammongy.helper.ui.baseui.BaseFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactsFragment : BaseFragment() , View.OnClickListener {
    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    private var listOfMyContacts = ArrayList<ContactEntities>()
    private var contactAdapter:ContactAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAddNewContact.setOnClickListener(this)
        binding.btnSearchContact.setOnClickListener(this)
        launch {
            withContext(Dispatchers.IO){
                listOfMyContacts =  HelperDataBase.getDataBaseInstance(requireActivity()).contactDao().getAllContacts() as ArrayList<ContactEntities>
            }
            if (listOfMyContacts.isEmpty()) {
                binding.emptyImageView.visibility = View.VISIBLE
            } else {
                binding.contactRecyclerView.setHasFixedSize(true)
                binding.contactRecyclerView.layoutManager = StaggeredGridLayoutManager(2 , StaggeredGridLayoutManager.VERTICAL)
                contactAdapter = ContactAdapter(requireContext(), listOfMyContacts)
                binding.contactRecyclerView.adapter = contactAdapter
            }
        }

        var deletedItem: String?
        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(
                    0,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false

                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    val position: Int = viewHolder.adapterPosition
                    val listContact: ContactEntities = listOfMyContacts[position]
                    launch {
                        context?.let {
                            HelperDataBase.getDataBaseInstance(context!!).contactDao().deleteSelectedContact(listContact)
                        }
                    }
                    deletedItem =
                        "Are You Sure You Want To Delete This " + listContact.contact_Name + "OR Undo Deleted .."
                    listOfMyContacts.removeAt(viewHolder.adapterPosition)
                    contactAdapter!!.notifyDataSetChanged()
                    Snackbar.make(binding.contactRecyclerView, deletedItem!!, Snackbar.LENGTH_LONG)
                        .setAction(
                            "Undo"
                        ) {

                            listOfMyContacts.add(position, listContact)
                            launch {
                                HelperDataBase.getDataBaseInstance(requireContext()).contactDao().saveNewContact(listContact)
                            }
                            contactAdapter!!.notifyItemInserted(position)

                        }.show()
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.contactRecyclerView)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_AddNewContact -> {
                requireActivity().startNewActivity(AddNewContact::class.java , 3)
            }
            R.id.btn_SearchContact ->{
                requireActivity().startSearchActivity(3)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}