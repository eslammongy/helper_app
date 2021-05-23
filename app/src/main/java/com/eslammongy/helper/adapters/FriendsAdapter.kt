package com.eslammongy.helper.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.database.entities.FriendModel
import com.eslammongy.helper.databinding.FriendsLayoutViewBinding

class FriendsAdapter(var context: Context, var listOFFriends: List<FriendModel>) :
    RecyclerView.Adapter<FriendsAdapter.FriendsViewModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewModel {

        return FriendsViewModel(
            FriendsLayoutViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ))

    }

    override fun onBindViewHolder(holder: FriendsViewModel, position: Int) {

        val friends = listOFFriends[position]
        holder.binding.friendName.text = friends.name
        holder.binding.friendImage.setImageResource(friends.image)


    }

    override fun getItemCount(): Int {
        return listOFFriends.size
    }

    class FriendsViewModel(val binding: FriendsLayoutViewBinding) : RecyclerView.ViewHolder(binding.root)

}