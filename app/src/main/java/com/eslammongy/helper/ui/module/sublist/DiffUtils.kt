package com.eslammongy.helper.ui.module.sublist

import androidx.recyclerview.widget.DiffUtil
import com.eslammongy.helper.database.entities.SubCheckList

class DiffUtils(
    private val oldSubList: ArrayList<SubCheckList>,
    private val newSubChList: ArrayList<SubCheckList>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldSubList.size
    }

    override fun getNewListSize(): Int {
        return newSubChList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldSubList[oldItemPosition].subCheckLId == newSubChList[newItemPosition].subCheckLId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldSubList[oldItemPosition].subCheckLId != newSubChList[newItemPosition].subCheckLId -> {
                false
            }
            oldSubList[oldItemPosition].subChl_Title != newSubChList[newItemPosition].subChl_Title -> {
                false
            }
            oldSubList[oldItemPosition].subChl_Time != newSubChList[newItemPosition].subChl_Time -> {
                false
            }
            oldSubList[oldItemPosition].subChl_Completed != newSubChList[newItemPosition].subChl_Completed -> {
                false
            }
            else -> true
        }
    }

}