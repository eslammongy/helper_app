package com.eslammongy.helper.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Contact_Table")
data class ContactEntities(

    val contact_Name: String,
    val contact_Phone: String,
    val contact_Email: String,
    val contact_Address: String,
    val contact_Color: String,
    val contact_Image: ByteArray?=null
){
    constructor() : this("", "", "", "", "",  null)

    @PrimaryKey(autoGenerate = true)
    var contactId: Int= 0
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContactEntities

        if (contactId != other.contactId) return false

        return true
    }

    override fun hashCode(): Int {
        return contactId
    }
}