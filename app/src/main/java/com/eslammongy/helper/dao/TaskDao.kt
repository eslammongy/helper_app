package com.eslammongy.helper.dao

import androidx.room.*
import com.eslammongy.helper.database.entities.TaskEntities

@Dao
interface TaskDao {

    @Query("SELECT * FROM Task_Table ORDER BY taskId DESC")
    fun getAllTasks():List<TaskEntities>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNewTask(taskEntities: TaskEntities)

    @Update
    suspend fun updateCurrentTask(taskEntities: TaskEntities)

    @Delete
    suspend fun deleteSelectedTask(taskEntities: TaskEntities)

}