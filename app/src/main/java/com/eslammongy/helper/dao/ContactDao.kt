package com.eslammongy.helper.dao

import androidx.room.*
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.database.entities.TaskEntities

@Dao
interface ContactDao {

    @Query("SELECT * FROM Contact_Table ORDER BY contactId DESC")
    fun getAllContacts():List<ContactEntities>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNewContact(contactEntities: ContactEntities)

    @Update
    suspend fun updateCurrentContact(contactEntities: ContactEntities)

    @Delete
    suspend fun deleteSelectedContact(contactEntities: ContactEntities)
}