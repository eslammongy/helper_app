package com.eslammongy.helper.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.databinding.ContactLayoutViewBinding
import com.eslammongy.helper.ui.contact.AddNewContact
import com.eslammongy.helper.utilis.GlideApp

class ContactAdapter(var context: Context) :
    RecyclerView.Adapter<ContactAdapter.ContactViewModel>(){

    private val diffUtilCallback = object : DiffUtil.ItemCallback<ContactEntities>(){
        override fun areItemsTheSame(oldItem: ContactEntities, newItem: ContactEntities): Boolean {
            return oldItem.contact_Name == newItem.contact_Name
        }

        override fun areContentsTheSame(oldItem: ContactEntities, newItem: ContactEntities): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffUtilCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewModel {

        return ContactViewModel(
            ContactLayoutViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ))

    }
    override fun onBindViewHolder(holder: ContactViewModel, position: Int) {
        val contactEntities = differ.currentList[position]
        holder.binding.contactLayoutName.text = contactEntities.contact_Name
        holder.binding.contactLayoutName.chipBackgroundColor =
            ColorStateList.valueOf(Integer.parseInt(contactEntities.contact_Color))
        holder.binding.contactLayoutColor.setCardBackgroundColor(Integer.parseInt(contactEntities.contact_Color))
       // holder.binding.mainView.strokeColor =Integer.parseInt(contactEntities.contact_Color)
        GlideApp.with(context).asBitmap().load(contactEntities.contact_Image).into(holder.binding.contactLayoutImage).clearOnDetach()
        holder.binding.root.setOnClickListener {

            val contactIntent = Intent(context , AddNewContact::class.java)
            contactIntent.putExtra("ID", contactEntities.contactId)
            contactIntent.putExtra("Name", contactEntities.contact_Name)
            contactIntent.putExtra("Phone",contactEntities.contact_Phone)
            contactIntent.putExtra("Email", contactEntities.contact_Email)
            contactIntent.putExtra("Address", contactEntities.contact_Address)
            contactIntent.putExtra("Color", contactEntities.contact_Color)
            contactIntent.putExtra("ImagePath", contactEntities.contact_Image)

            context.startActivity(contactIntent)
            (context as Activity).finish()
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    class ContactViewModel(val binding: ContactLayoutViewBinding) : RecyclerView.ViewHolder(binding.root)

}