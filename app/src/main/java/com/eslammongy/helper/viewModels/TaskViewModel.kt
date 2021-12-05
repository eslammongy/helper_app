package com.eslammongy.helper.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.data.entities.TaskEntities
import com.eslammongy.helper.repository.TasksRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application):AndroidViewModel(application){
    private val tasksRepo: TasksRepo
    var allTasksList:LiveData<List<TaskEntities>>
    init {
        val taskDao = HelperDataBase.getDataBaseInstance(application).taskDao()
        tasksRepo = TasksRepo(taskDao)
        allTasksList = tasksRepo.allTasksList

    }
    fun saveNewTask(taskEntities: TaskEntities) = viewModelScope.launch (Dispatchers.IO){
        tasksRepo.saveNewTask(taskEntities)
    }

    fun updateCurrentTask(taskEntities: TaskEntities) = viewModelScope.launch (Dispatchers.IO){
        tasksRepo.updateCurrentTask(taskEntities)
    }

    fun  deleteSelectedTask(taskEntities: TaskEntities) = viewModelScope.launch (Dispatchers.IO){
        tasksRepo.deleteCurrentTask(taskEntities)
    }

     suspend fun getSingleContact(contactID:Int) = tasksRepo.getSingleContact(contactID)

    suspend fun getSingleTask(taskID:Int) = tasksRepo.getSingleTask(taskID)

    suspend fun getSingleTaskByName(title:String) = tasksRepo.getSingleTaskByName(title)

    fun searchInTaskDatabase(searchQuery: String): LiveData<List<TaskEntities>> {
        return tasksRepo.searchInTaskDatabase(searchQuery).asLiveData()
    }


}