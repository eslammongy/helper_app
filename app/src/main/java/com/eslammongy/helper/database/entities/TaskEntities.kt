package com.eslammongy.helper.database.entities

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Task_Table")
data class TaskEntities (

    val task_Title:String,
    val task_Desc:String,
    val task_Time:String,
    val task_Date:String,
    val task_Link:String,
    val task_Color:String,
    val task_Image:Bitmap
        ){
    @PrimaryKey(autoGenerate = true)
    val taskId:Int =0
}