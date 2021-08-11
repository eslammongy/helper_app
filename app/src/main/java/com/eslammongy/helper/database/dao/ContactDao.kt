package com.eslammongy.helper.database.dao

import androidx.room.*
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.database.entities.TaskEntities

@Dao
interface ContactDao {

    @Query("SELECT * FROM Contact_Table ORDER BY contactId DESC")
    suspend fun getAllContacts():List<ContactEntities>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun saveNewContact(contactEntities: ContactEntities)

    @Update
     suspend fun updateCurrentContact(contactEntities: ContactEntities)

    @Delete
     suspend fun deleteSelectedContact(contactEntities: ContactEntities)

    @Query("SELECT * FROM contact_table WHERE contact_Name LIKE :searchQuery")
    suspend fun searchInContactDataBase(searchQuery: String): List<ContactEntities>

    @Query("SELECT * FROM task_table WHERE friend_id LIKE :friendID")
    suspend fun getTaskWithFriend(friendID: Int): List<TaskEntities>
}