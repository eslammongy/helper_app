package com.eslammongy.helper.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.database.entities.TaskEntities
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM Task_Table ORDER BY taskId DESC")
    fun getAllTasks():LiveData<List<TaskEntities>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNewTask(taskEntities: TaskEntities)

    @Update
    suspend fun updateCurrentTask(taskEntities: TaskEntities)

    @Delete
    suspend fun deleteSelectedTask(taskEntities: TaskEntities)

    @Query("SELECT * FROM task_table WHERE title LIKE :searchQuery")
    fun searchInTaskDataBase(searchQuery: String):Flow<List<TaskEntities>>

    @Query("SELECT * FROM contact_table WHERE contactId LIKE :friendID")
    suspend fun getSingleContact(friendID: Int): ContactEntities

    @Query("SELECT * FROM task_table WHERE taskId LIKE :taskID")
    suspend fun getSingleTask(taskID: Int): TaskEntities

    @Query("SELECT * FROM task_table WHERE title LIKE :taskTitle")
    suspend fun getSingleTaskByName(taskTitle: String): TaskEntities

}