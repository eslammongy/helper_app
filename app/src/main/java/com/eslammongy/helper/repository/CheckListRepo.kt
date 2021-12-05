package com.eslammongy.helper.repository

import androidx.lifecycle.LiveData
import com.eslammongy.helper.database.dao.CheckListDao
import com.eslammongy.helper.data.entities.CheckListEntity
import com.eslammongy.helper.data.entities.SubCheckList
import kotlinx.coroutines.flow.Flow

class CheckListRepo(private val chListDao: CheckListDao) {

    var getAllChLists:LiveData<List<CheckListEntity>> = chListDao.getAllCheckLists()
    suspend fun saveNewCheckList(checkListEntity: CheckListEntity) = chListDao.saveNewCheckList(checkListEntity)

    suspend fun updateCurrentChList(checkListEntity: CheckListEntity) = chListDao.updateCurrentCheckList(checkListEntity)

    suspend fun deleteCurrentChList(checkListEntity: CheckListEntity) = chListDao.deleteSelectedCheckList(checkListEntity)

    fun searchInChListDatabase(searchQuery: String): Flow<List<CheckListEntity>> {
        return chListDao.searchInCheckListDataBase(searchQuery)
    }

    suspend fun getSingleChList(chListID:Int) = chListDao.getSingleChList(chListID)

    // subCheckList Repo
    suspend fun saveNewSubCheckList(subCheckList: SubCheckList) = chListDao.saveNewSubCheckList(subCheckList)

    suspend fun deleteCurrentSubChList(subCheckList: SubCheckList) = chListDao.deleteSelectedSubCheckList(subCheckList)

    fun getAllSubChList(parentChLID:Int): LiveData<List<SubCheckList>> {
        return chListDao.getAllSubCheckLists(parentChLID)
    }


}