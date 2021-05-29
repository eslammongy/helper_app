package com.eslammongy.helper.database.dao

import androidx.room.*
import com.eslammongy.helper.database.entities.TaskEntities

@Dao
interface TaskDao {

    @Query("SELECT * FROM Task_Table ORDER BY taskId DESC")
    fun getAllTasks():List<TaskEntities>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun saveNewTask(taskEntities: TaskEntities)

    @Update
     fun updateCurrentTask(taskEntities: TaskEntities)

    @Delete
     fun deleteSelectedTask(taskEntities: TaskEntities)

    @Query("SELECT * FROM task_table WHERE friend_name LIKE :searchQuery")
    fun getTaskWithFriend(searchQuery: String): List<TaskEntities>

    @Query("SELECT * FROM task_table WHERE title LIKE :searchQuery")
    fun searchInTaskDataBase(searchQuery: String): List<TaskEntities>

}