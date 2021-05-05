package com.eslammongy.helper.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CheckList_Table")
data class CheckListEntity(
    @PrimaryKey(autoGenerate = true)
    val checkListId: Int = 0,
    val checkList_Title: String,
    val checkList_Time: String,
    val checkList_Date: String,
    val checkList_Color: String,
    val checkList_Completed: Boolean,
)