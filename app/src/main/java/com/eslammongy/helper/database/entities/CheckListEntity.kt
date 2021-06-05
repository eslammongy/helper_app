package com.eslammongy.helper.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CheckList_Table")
data class CheckListEntity(

    val checkList_Title: String,
    val checkList_Time: String,
    val checkList_Date: String,
    val checkList_Color: String,
    var checkList_Completed: Boolean,
){

    constructor():this("" , "" , "" , "" , false)
    @PrimaryKey(autoGenerate = true)
    var checkListId: Int = 0
}