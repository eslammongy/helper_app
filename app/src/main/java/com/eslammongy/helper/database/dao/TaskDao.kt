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

}