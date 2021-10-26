package com.eslammongy.helper.ui.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.eslammongy.helper.R
import com.eslammongy.helper.adapters.ContactAdapter
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.databinding.FragmentContactsBinding
import com.eslammongy.helper.utilis.startNewActivity
import com.eslammongy.helper.utilis.startSearchActivity
import com.eslammongy.helper.viewModels.ContactViewMode
import com.google.android.material.snackbar.Snackbar

class ContactsFragment:Fragment() , View.OnClickListener {
    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private lateinit var contactViewMode: ContactViewMode
    private lateinit var contactAdapter: ContactAdapter
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

        contactViewMode = ViewModelProvider(this , ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))
            .get(ContactViewMode::class.java)

        displayTasksRecyclerView()
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
                    val listContact: ContactEntities = contactAdapter.differ.currentList[position]
                    val listSize = contactAdapter.differ.currentList.size
                    deletedItem =
                        "Are You Sure You Want To Delete This " + listContact.contact_Name + "OR Undo Deleted .."
                    contactViewMode.deleteCurrentContact(listContact)
                    contactAdapter.notifyDataSetChanged()
                    Snackbar.make(binding.contactRecyclerView, deletedItem!!, Snackbar.LENGTH_LONG)
                        .setAction(
                            "Undo"
                        ) {
                             contactViewMode.saveNewContact(listContact)
                            contactAdapter.notifyItemRangeInserted(listSize , contactAdapter.differ.currentList.size-1)

                        }.show()
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.contactRecyclerView)
    }

    private fun displayTasksRecyclerView(){
       contactAdapter = ContactAdapter(requireContext())
        binding.contactRecyclerView.apply {
            binding.contactRecyclerView.layoutManager =  StaggeredGridLayoutManager(2 , StaggeredGridLayoutManager.VERTICAL)
            binding.contactRecyclerView.setHasFixedSize(true)
            adapter = contactAdapter
        }
        contactViewMode.getAllContacts.observe(viewLifecycleOwner , {
            if (it.isEmpty()){
                binding.emptyImageView.visibility = View.VISIBLE
                contactAdapter.differ.submitList(it)
            }else{
                binding.emptyImageView.visibility = View.GONE
                contactAdapter.differ.submitList(it)
            }
        })

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