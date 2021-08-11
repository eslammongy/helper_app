package com.eslammongy.helper.ui.module.checklist

import androidx.recyclerview.widget.DiffUtil
import com.eslammongy.helper.database.entities.CheckListEntity

class ChlDiffUtils(
    private val oldList: ArrayList<CheckListEntity>,
    private val newList: ArrayList<CheckListEntity>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].checkListId == newList[newItemPosition].checkListId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].checkListId != newList[newItemPosition].checkListId -> {
                false
            }
            oldList[oldItemPosition].checkList_Title != newList[newItemPosition].checkList_Title -> {
                false
            }
            oldList[oldItemPosition].checkList_Date != newList[newItemPosition].checkList_Date -> {
                false
            }
            oldList[oldItemPosition].checkList_Completed != newList[newItemPosition].checkList_Completed -> {
                false
            }
            else -> true
        }
    }
}