package com.eslammongy.helper.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.SubCheckList
import com.eslammongy.helper.databinding.SubChecklistLayoutBinding

class SubChlAdapter(var context: Context) : RecyclerView.Adapter<SubChlAdapter.SubChlViewHolder>() {

    private var oldSubChList = ArrayList<SubCheckList>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubChlViewHolder {
        return SubChlViewHolder(
            SubChecklistLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: SubChlViewHolder, position: Int) {

        val subChlWithParentChl = oldSubChList[position]
        holder.binding.materialCheckBox.isChecked = subChlWithParentChl.subChl_Completed
        if (!subChlWithParentChl.subChl_Completed) holder.binding.root.alpha = 1.0F else holder.binding.root.alpha = 0.3F
        holder.binding.chlLayoutTitle.text = subChlWithParentChl.subChl_Title
        holder.binding.chlLayoutTime.text = subChlWithParentChl.subChl_Time
        holder.binding.chlCircularCardView.setCardBackgroundColor(Integer.parseInt(subChlWithParentChl.subChl_Color))
        holder.binding.materialCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                holder.binding.root.alpha = 0.3F
                HelperDataBase.getDataBaseInstance(context).checkListDao().getSubChlCompleteStatus(subChlWithParentChl.subCheckLId , isChecked)
               // Toast.makeText(context, "${subChlWithParentChl.subChl_Completed}!", Toast.LENGTH_LONG).show()

            }else{
                holder.binding.root.alpha = 1.0F
                HelperDataBase.getDataBaseInstance(context).checkListDao().getSubChlCompleteStatus(subChlWithParentChl.subCheckLId , isChecked)
                    // Toast.makeText(context, "${subChlWithParentChl.subChl_Completed}!", Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun getItemCount(): Int {
        return oldSubChList.size
    }

    fun setDate(newSubChlList: ArrayList<SubCheckList>) {
        val diffUtil = DiffUtils(oldSubChList, newSubChlList)
        val diffResults:DiffUtil.DiffResult = DiffUtil.calculateDiff(diffUtil)
        oldSubChList = newSubChlList
        diffResults.dispatchUpdatesTo(this)
    }

    class SubChlViewHolder(val binding: SubChecklistLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

}
