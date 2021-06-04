package com.eslammongy.helper.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.database.entities.SubCheckList
import com.eslammongy.helper.databinding.SubChecklistLayoutBinding

class SubChlAdapter (var context: Context, var listOfSubCheckList: ArrayList<SubCheckList>):RecyclerView.Adapter<SubChlAdapter.SubChlViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubChlViewHolder {
        return SubChlViewHolder(SubChecklistLayoutBinding.inflate(LayoutInflater.from(parent.context) , parent , false))

    }

    override fun onBindViewHolder(holder: SubChlViewHolder, position: Int) {

        val subChlWithParentChl = listOfSubCheckList[position]
            holder.binding.chlLayoutTitle.text = subChlWithParentChl.subChl_Title
            holder.binding.chlCircularCardView.setCardBackgroundColor(
                Integer.parseInt(
                    subChlWithParentChl.subChl_Color
                )
            )
            holder.binding.materialCheckBox.isChecked =
                subChlWithParentChl.subChl_Completed



    }

    override fun getItemCount(): Int {
       return listOfSubCheckList.size

    }

    class SubChlViewHolder(val binding:SubChecklistLayoutBinding):RecyclerView.ViewHolder(binding.root)

}