package com.eslammongy.helper.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.eslammongy.helper.database.HelperDataBase
import com.eslammongy.helper.data.entities.CheckListEntity
import com.eslammongy.helper.data.entities.SubCheckList
import com.eslammongy.helper.repository.CheckListRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChListViewModel(application: Application):AndroidViewModel(application) {

    private val checkListRepo:CheckListRepo
    var allCheckLists:LiveData<List<CheckListEntity>>

    init {
        val chListDao = HelperDataBase.getDataBaseInstance(application).checkListDao()
        checkListRepo = CheckListRepo(chListDao)
        allCheckLists = checkListRepo.getAllChLists
    }

    fun saveNewChLIst(chListEntity:CheckListEntity) = viewModelScope.launch (Dispatchers.IO){
        checkListRepo.saveNewCheckList(chListEntity)
    }

    fun updateCurrentChList(chListEntity:CheckListEntity) = viewModelScope.launch (Dispatchers.IO){
        checkListRepo.updateCurrentChList(chListEntity)
    }

    fun deleteCurrentChLIst(chListEntity:CheckListEntity) = viewModelScope.launch (Dispatchers.IO){
        checkListRepo.deleteCurrentChList(chListEntity)
    }

    fun searchInChListDatabase(searchQuery: String): LiveData<List<CheckListEntity>> {
        return checkListRepo.searchInChListDatabase(searchQuery).asLiveData()
    }

    suspend fun getSingleChList(chListID:Int) = checkListRepo.getSingleChList(chListID)


    // sub Check List Repo

    fun saveNewSubChList(subCheckList: SubCheckList) = viewModelScope.launch(Dispatchers.IO){
        checkListRepo.saveNewSubCheckList(subCheckList)
    }

    fun deleteCurrentSubChList(subCheckList: SubCheckList)= viewModelScope.launch(Dispatchers.IO){
        checkListRepo.deleteCurrentSubChList(subCheckList)
    }

    fun getAllSubChList(parentChLID:Int): LiveData<List<SubCheckList>> {
        return checkListRepo.getAllSubChList(parentChLID)
    }

}