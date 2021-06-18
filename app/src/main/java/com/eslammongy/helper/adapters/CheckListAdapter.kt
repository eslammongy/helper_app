package com.eslammongy.helper.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.database.entities.CheckListEntity
import com.eslammongy.helper.databinding.ChecklistLayoutViewBinding
import com.eslammongy.helper.ui.AddNewCheckListActivity


class CheckListAdapter(var context: Context ) :
    RecyclerView.Adapter<CheckListAdapter.CheckListViewHolder>() {

    private var oldChList = ArrayList<CheckListEntity>()

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
       val chlEntity = oldChList[position]
       holder.binding.chlCompleted.isChecked = chlEntity.checkList_Completed
        if (!chlEntity.checkList_Completed){
            holder.binding.root.alpha = 1.0F
            holder.binding.line1.visibility = View.GONE
        }else{
            holder.binding.root.alpha = 0.3F
            holder.binding.line1.visibility = View.VISIBLE
        }
        holder.binding.chlLayoutTitle.text = chlEntity.checkList_Title
        holder.binding.chlLayoutTime.text = chlEntity.checkList_Time
        holder.binding.chlCircularCardView.setCardBackgroundColor(Integer.parseInt(chlEntity.checkList_Color))
        holder.binding.chlLayoutTitle.chipBackgroundColor =  ColorStateList.valueOf(Integer.parseInt(chlEntity.checkList_Color))

        holder.binding.chlCompleted.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                holder.binding.root.alpha = 0.3F
                HelperDataBase.getDataBaseInstance(context).checkListDao().getCompleteStatus(chlEntity.checkListId , true)
                holder.binding.line1.visibility = View.VISIBLE
                //Toast.makeText(context, "${chlEntity.checkList_Completed}!", Toast.LENGTH_LONG).show()
            } else {
                holder.binding.root.alpha = 1.0F
                HelperDataBase.getDataBaseInstance(context).checkListDao().getCompleteStatus(chlEntity.checkListId , false)
                holder.binding.line1.visibility = View.GONE
               // Toast.makeText(context, "${chlEntity.checkList_Completed}!", Toast.LENGTH_LONG).show()
            }
        }

        holder.binding.root.setOnClickListener {
            val intent = Intent(context, AddNewCheckListActivity::class.java)
            intent.putExtra("chlID", chlEntity.checkListId)
            intent.putExtra("chlTitle", chlEntity.checkList_Title)
            intent.putExtra("chlTime", chlEntity.checkList_Time)
            intent.putExtra("chlDate", chlEntity.checkList_Date)
            intent.putExtra("chlComplete", chlEntity.checkList_Completed)
            intent.putExtra("chlColor", chlEntity.checkList_Color)
            //Toast.makeText(context, "CheckList Saved. ${chlEntity.checkList_Completed} !", Toast.LENGTH_LONG).show()
            context.startActivity(intent)
            (context as Activity)
                .finish()

        }
    }

    fun setData(newChList:ArrayList<CheckListEntity>){
        val chlDiffUtils = ChlDiffUtils(oldChList, newChList)
        val diffResults = DiffUtil.calculateDiff(chlDiffUtils)
        oldChList = newChList
        diffResults.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return oldChList.size
    }
    class CheckListViewHolder(val binding: ChecklistLayoutViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnItemSelectedListener{
        fun onClicked(checkListEntity: CheckListEntity , position: Int)
    }
}