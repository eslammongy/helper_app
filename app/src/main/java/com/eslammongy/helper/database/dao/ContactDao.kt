package com.eslammongy.helper.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.database.entities.TaskEntities
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Query("SELECT * FROM Contact_Table ORDER BY contactId DESC")
     fun getAllContacts():LiveData<List<ContactEntities>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun saveNewContact(contactEntities: ContactEntities)

    @Update
     suspend fun updateCurrentContact(contactEntities: ContactEntities)

    @Delete
     suspend fun deleteSelectedContact(contactEntities: ContactEntities)

    @Query("SELECT * FROM contact_table WHERE contact_Name LIKE :searchQuery")
     fun searchInContactDataBase(searchQuery: String): Flow<List<ContactEntities>>

    @Query("SELECT * FROM task_table WHERE friend_id LIKE :friendID")
     fun getTaskWithFriend(friendID: Int):LiveData<List<TaskEntities>>
}