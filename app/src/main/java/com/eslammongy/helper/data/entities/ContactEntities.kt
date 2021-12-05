package com.eslammongy.helper.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Contact_Table")
data class ContactEntities(

    val contact_Name: String,
    val contact_Phone: String,
    val contact_Email: String,
    val contact_Address: String,
    val contact_Color: String,
    val contact_Image: String
){
    constructor() : this("", "", "", "", "",  "")

    @PrimaryKey(autoGenerate = true)
    var contactId: Int= 0

}