package com.eslammongy.helper.database.dao

import androidx.room.*
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.database.entities.TaskEntities

@Dao
interface TaskDao {

    @Query("SELECT * FROM Task_Table ORDER BY taskId DESC")
    suspend fun getAllTasks():List<TaskEntities>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNewTask(taskEntities: TaskEntities)

    @Update
    suspend fun updateCurrentTask(taskEntities: TaskEntities)

    @Delete
    suspend fun deleteSelectedTask(taskEntities: TaskEntities)

    @Query("SELECT * FROM task_table WHERE title LIKE :searchQuery")
    suspend fun searchInTaskDataBase(searchQuery: String): List<TaskEntities>

    @Query("SELECT * FROM contact_table WHERE contactId LIKE :friendID")
    suspend fun getSingleContact(friendID: Int): ContactEntities

    @Query("SELECT * FROM task_table WHERE taskId LIKE :taskID")
    suspend fun getSingleTask(taskID: Int): TaskEntities

}