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
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val taskImage: ByteArray? = null,
    @ColumnInfo(name = "friend_name")
    val taskFriendName: String = "",
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val taskFriendImage: ByteArray? = null,

    ) {

    @PrimaryKey(autoGenerate = true)
    var taskId: Int = 0

    constructor() : this("", "", "", "", "", "", null)

    @Override
    override fun toString(): String {
        return ("$taskTitle : $taskDesc : $taskTime : $taskDate : $taskLink")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TaskEntities

        if (taskImage != null) {
            if (other.taskImage == null) return false
            if (!taskImage.contentEquals(other.taskImage)) return false
        } else if (other.taskImage != null) return false
        if (taskFriendImage != null) {
            if (other.taskFriendImage == null) return false
            if (!taskFriendImage.contentEquals(other.taskFriendImage)) return false
        } else if (other.taskFriendImage != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = taskImage?.contentHashCode() ?: 0
        result = 31 * result + (taskFriendImage?.contentHashCode() ?: 0)
        return result
    }


}