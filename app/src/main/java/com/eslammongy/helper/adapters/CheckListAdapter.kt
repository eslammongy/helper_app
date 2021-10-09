package com.eslammongy.helper.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.CheckListEntity
import com.eslammongy.helper.databinding.ChecklistLayoutViewBinding
import com.eslammongy.helper.ui.module.checklist.AddNewCheckList


class CheckListAdapter(var context: Context) : RecyclerView.Adapter<CheckListAdapter.CheckListViewHolder>() {

    private val diffUtilCallback = object : DiffUtil.ItemCallback<CheckListEntity>() {
        override fun areItemsTheSame(oldItem: CheckListEntity, newItem: CheckListEntity): Boolean {
            return oldItem.checkList_Title == newItem.checkList_Title
        }
        override fun areContentsTheSame(
            oldItem: CheckListEntity,
            newItem: CheckListEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffUtilCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckListViewHolder {
        return CheckListViewHolder(
            ChecklistLayoutViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CheckListViewHolder, position: Int) {
        val chlEntity = differ.currentList[position]
        holder.binding.chlLayoutTitle.text = chlEntity.checkList_Title
        holder.binding.chlLayoutTime.text = chlEntity.checkList_Date
        holder.binding.chlCircularCardView.setCardBackgroundColor(Integer.parseInt(chlEntity.checkList_Color))
        holder.binding.chlLayoutTitle.chipBackgroundColor =
            ColorStateList.valueOf(Integer.parseInt(chlEntity.checkList_Color))
        holder.binding.chlCompleted.isChecked = chlEntity.checkList_Completed
        if (chlEntity.checkList_Completed) {
            holder.binding.parentView.alpha = 0.45F
            holder.binding.line1.visibility = View.VISIBLE
        } else {
            holder.binding.parentView.alpha = 1.0F
            holder.binding.line1.visibility = View.GONE
        }
        holder.binding.chlCompleted.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                holder.binding.parentView.alpha = 0.45F
                HelperDataBase.getDataBaseInstance(context).checkListDao()
                    .getCompleteStatus(chlEntity.checkListId, isChecked)
                holder.binding.line1.visibility = View.VISIBLE
            } else {
                holder.binding.parentView.alpha = 1.0F
                HelperDataBase.getDataBaseInstance(context).checkListDao()
                    .getCompleteStatus(chlEntity.checkListId, isChecked)
                holder.binding.line1.visibility = View.GONE
            }
        }

        holder.binding.root.setOnClickListener {
            moveToCurrentChl(chlEntity)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class CheckListViewHolder(val binding: ChecklistLayoutViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    private fun moveToCurrentChl(chlEntity: CheckListEntity) {
        Intent(context, AddNewCheckList::class.java).apply {
            putExtra("chlID", chlEntity.checkListId)
            putExtra("chlTitle", chlEntity.checkList_Title)
            putExtra("chlDate", chlEntity.checkList_Date)
            putExtra("chlComplete", chlEntity.checkList_Completed)
            putExtra("chlColor", chlEntity.checkList_Color)
            context.startActivity(this)
            (context as Activity)
                .finish()
        }
    }

}