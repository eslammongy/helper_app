package com.eslammongy.helper.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SubChl_Table")
data class SubCheckList(
    val subChl_Title: String,
    val subChl_Time: String,
    val subChl_Color: String,
    var subChl_Completed: Boolean,
    var parentChListId: Int
) {

    @PrimaryKey(autoGenerate = true)
    var subCheckLId: Int = 0


}
