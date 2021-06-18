package com.eslammongy.helper.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.eslammongy.helper.adapters.ContactAdapter
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.databinding.FragmentContactBinding
import com.google.android.material.snackbar.Snackbar

class ContactFragment : Fragment() {
    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!
    private var listOfMyContacts = ArrayList<ContactEntities>()
    private var contactAdapter: ContactAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayRecyclerView()
        var deletedItem: String?
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
                    val listContacts: ContactEntities = listOfMyContacts[position]
                    HelperDataBase.getDataBaseInstance(activity!!).contactDao()
                        .deleteSelectedContact(listContacts)
                    deletedItem =
                        "Are You Sure You Want To Delete This " + listContacts.contact_Name + "OR Undo Deleted .."
                    listOfMyContacts.removeAt(viewHolder.adapterPosition)
                    contactAdapter!!.notifyDataSetChanged()
                    Snackbar.make(binding.contactRecyclerView, deletedItem!!, Snackbar.LENGTH_LONG)
                        .setAction(
                            "Undo"
                        ) {

                            listOfMyContacts.add(position, listContacts)
                            HelperDataBase.getDataBaseInstance(activity!!).contactDao()
                                .saveNewContact(listContacts)
                            contactAdapter!!.notifyItemInserted(position)

                        }.show()
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.contactRecyclerView)

    }

    private fun displayRecyclerView() {
        listOfMyContacts = HelperDataBase.getDataBaseInstance(requireContext()).contactDao().getAllContacts() as ArrayList<ContactEntities>

        if (listOfMyContacts.isEmpty()){
            binding.emptyImageView.visibility = View.VISIBLE
        }else{
            contactAdapter = ContactAdapter(requireContext() , listOfMyContacts)
            binding.contactRecyclerView.setHasFixedSize(true)
            binding.contactRecyclerView.layoutManager = StaggeredGridLayoutManager(2 , StaggeredGridLayoutManager.VERTICAL)
            binding.contactRecyclerView.adapter = contactAdapter!!
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}