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

    ) {

    @PrimaryKey(autoGenerate = true)
    var taskId: Int = 0

    private constructor() : this("", "", "", "", "", "", null)

    @Override
    override fun toString(): String {
        return ("$taskTitle : $taskDesc : $taskTime : $taskDate : $taskLink")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TaskEntities

        if (!taskImage.contentEquals(other.taskImage)) return false

        return true
    }

    override fun hashCode(): Int {
        return taskImage.contentHashCode() ?: 0
    }
}