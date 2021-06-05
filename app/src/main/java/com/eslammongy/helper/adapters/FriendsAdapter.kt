package com.eslammongy.helper.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.database.converter.Converter
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.databinding.FriendsLayoutViewBinding

class FriendsAdapter(
    var context: Context,
    var listOFFriends: List<ContactEntities>,
    onItemClickListener: OnItemClickerListener
) :
    RecyclerView.Adapter<FriendsAdapter.FriendsViewModel>() {

    private var checkedPosition:Int = -1
    private var onItemClickListener:OnItemClickerListener? = null
    init {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewModel {

        return FriendsViewModel(
            FriendsLayoutViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: FriendsViewModel, position: Int) {

        val friends = listOFFriends[position]
        holder.binding.friendName.text = friends.contact_Name
        val imageConverter = Converter()
        holder.binding.friendImage.setImageBitmap(imageConverter.toBitMap(friends.contact_Image!!))
       holder.binding.root.setOnClickListener {
            checkedPosition = position
           onItemClickListener!!.onClicked(listOFFriends[position], position)
           onItemClickListener!!.onItemClickListener(position , holder.itemView)
       }

        if (checkedPosition == position){

            holder.binding.selectedFriend.visibility = View.VISIBLE
        }else{

            holder.binding.selectedFriend.visibility = View.GONE

        }

    }
    fun selectedItem() {
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listOFFriends.size
    }

    class FriendsViewModel(val binding: FriendsLayoutViewBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickerListener{
        fun onClicked(contactEntities: ContactEntities, position: Int)
        fun onItemClickListener(position: Int, view: View?)
    }


}

