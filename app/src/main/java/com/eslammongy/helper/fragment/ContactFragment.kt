package com.eslammongy.helper.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.eslammongy.helper.adapters.ContactAdapter
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.databinding.FragmentContactBinding

class ContactFragment : Fragment() {
    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!
    private var listOfMyContacts = ArrayList<ContactEntities>()
    private var contactAdapter:ContactAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =FragmentContactBinding.inflate(inflater, container, false)

        listOfMyContacts = HelperDataBase.getDataBaseInstance(activity!!).contactDao().getAllContacts() as ArrayList<ContactEntities>
        contactAdapter = ContactAdapter(activity!! , listOfMyContacts)
        binding.contactRecyclerView.setHasFixedSize(true)
        binding.contactRecyclerView.layoutManager = StaggeredGridLayoutManager(2 , StaggeredGridLayoutManager.VERTICAL)
        binding.contactRecyclerView.adapter = contactAdapter!!
        return binding.root
    }

}