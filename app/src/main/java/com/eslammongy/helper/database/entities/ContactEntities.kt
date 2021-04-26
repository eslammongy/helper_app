package com.eslammongy.helper.database.entities

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Contact_Table")
data class ContactEntities(
    val contact_Name: String,
    val contact_Phone: String,
    val contact_Email: String,
    val contact_Address: String,
    val contact_Color: String,
    val contact_Image: Bitmap
) {

    @PrimaryKey(autoGenerate = true)
    val contactId: Int = 0
}