package com.eslammongy.helper.ui.module.task

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eslammongy.helper.database.entities.TaskEntities
import com.eslammongy.helper.databinding.TaskLayoutViewBinding
import com.eslammongy.helper.helpers.GlideApp


class TaskAdapter(var context: Context, var listOFTasks: List<TaskEntities>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewModel>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewModel {

        return TaskViewModel(
            TaskLayoutViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ))
    }
    override fun onBindViewHolder(holder: TaskViewModel, position: Int) {
        val taskModel = listOFTasks[position]
        holder.binding.taskLayoutTitle.text = taskModel.taskTitle
        holder.binding.taskLayoutTitle.chipBackgroundColor =
            ColorStateList.valueOf(Integer.parseInt(taskModel.taskColor))
        holder.binding.taskLayoutTime.text = taskModel.taskTime
        holder.binding.taskLayoutDate.text = taskModel.taskDate
        holder.binding.circularCardView.chipBackgroundColor =
            ColorStateList.valueOf(Integer.parseInt(taskModel.taskColor))

        GlideApp.with(context).asBitmap().load(taskModel.taskImage).into(holder.binding.taskLayoutImage).clearOnDetach()

        holder.binding.root.setOnClickListener {

            val taskIntent = Intent(context , AddNewTask::class.java)
            taskIntent.putExtra("ID", taskModel.taskId)
            taskIntent.putExtra("Title", taskModel.taskTitle)
            taskIntent.putExtra("Content", taskModel.taskDesc)
            taskIntent.putExtra("Date", taskModel.taskDate)
            taskIntent.putExtra("Time", taskModel.taskTime)
            taskIntent.putExtra("Link", taskModel.taskLink)
            taskIntent.putExtra("Color", taskModel.taskColor)
            taskIntent.putExtra("TaskImage", taskModel.taskImage)
            taskIntent.putExtra("TaskFriendID", taskModel.taskFriendID)
            context.startActivity(taskIntent)
            (context as Activity).finish()

        }
    }

    override fun getItemCount(): Int {
        return listOFTasks.size
    }

    class TaskViewModel(val binding: TaskLayoutViewBinding ) : RecyclerView.ViewHolder(binding.root)
}
