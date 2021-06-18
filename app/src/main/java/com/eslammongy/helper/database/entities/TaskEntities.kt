package com.eslammongy.helper.database.entities

import android.graphics.Bitmap
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
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val taskImage: Bitmap? = null,
    @ColumnInfo(name = "friend_name")
    val taskFriendName: String = "",
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val taskFriendImage: Bitmap? = null,

    ) {

    @PrimaryKey(autoGenerate = true)
    var taskId: Int = 0

    constructor() : this("", "", "", "", "", "", null)

    @Override
    override fun toString(): String {
        return ("$taskTitle : $taskDesc : $taskTime : $taskDate : $taskLink")
    }

}
