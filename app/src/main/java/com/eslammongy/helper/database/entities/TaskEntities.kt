package com.eslammongy.helper.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Task_Table")
data class TaskEntities(

    @ColumnInfo(name = "title")
    var taskTitle: String = "",
    @ColumnInfo(name = "content")
    var taskDesc: String = "",
    @ColumnInfo(name = "time")
    var taskTime: String = "",
    @ColumnInfo(name = "date")
    var taskDate: String = "",
    @ColumnInfo(name = "link")
    var taskLink: String = "",
    @ColumnInfo(name = "color_task")
    val taskColor: String = "",
    @ColumnInfo(name = "task_image")
    var taskImage: String,
    @ColumnInfo(name = "friend_id")
    val taskFriendID: Int,

    ) {

    @PrimaryKey(autoGenerate = true)
    var taskId: Int = 0

    constructor() : this("", "", "", "", "", "" ,"" ,0)

    @Override
    override fun toString(): String {
        return ("$taskTitle : $taskDesc : $taskTime : $taskDate : $taskLink")
    }

}
