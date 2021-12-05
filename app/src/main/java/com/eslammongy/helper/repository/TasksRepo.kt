package com.eslammongy.helper.repository

import androidx.lifecycle.LiveData
import com.eslammongy.helper.database.dao.TaskDao
import com.eslammongy.helper.data.entities.TaskEntities
import kotlinx.coroutines.flow.Flow

class TasksRepo(private var taskDao: TaskDao) {


    var allTasksList: LiveData<List<TaskEntities>> = taskDao.getAllTasks()

    suspend fun saveNewTask(taskEntities: TaskEntities) = taskDao.saveNewTask(taskEntities)

    suspend fun updateCurrentTask(taskEntities: TaskEntities) = taskDao.updateCurrentTask(taskEntities)

    suspend fun deleteCurrentTask(taskEntities: TaskEntities) = taskDao.deleteSelectedTask(taskEntities)

    suspend fun getSingleContact(contactID:Int) = taskDao.getSingleContact(contactID)

    suspend fun getSingleTask(taskID:Int) = taskDao.getSingleTask(taskID)

    suspend fun getSingleTaskByName(title:String) = taskDao.getSingleTaskByName(title)

    fun searchInTaskDatabase(searchQuery: String): Flow<List<TaskEntities>> {
        return taskDao.searchInTaskDataBase(searchQuery)
    }

}