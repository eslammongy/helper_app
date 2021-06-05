package com.eslammongy.helper.database.dao

import androidx.room.*
import com.eslammongy.helper.database.entities.ContactEntities

@Dao
interface ContactDao {

    @Query("SELECT * FROM Contact_Table ORDER BY contactId DESC")
    fun getAllContacts():List<ContactEntities>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun saveNewContact(contactEntities: ContactEntities)

    @Update
     fun updateCurrentContact(contactEntities: ContactEntities)

    @Delete
     fun deleteSelectedContact(contactEntities: ContactEntities)

    @Query("SELECT * FROM contact_table WHERE contact_Name LIKE :searchQuery")
    fun searchInContactDataBase(searchQuery: String): List<ContactEntities>
}