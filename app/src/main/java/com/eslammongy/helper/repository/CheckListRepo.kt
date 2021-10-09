package com.eslammongy.helper.repository

import androidx.lifecycle.LiveData
import com.eslammongy.helper.database.dao.CheckListDao
import com.eslammongy.helper.database.entities.CheckListEntity

class CheckListRepo(private val chListDao: CheckListDao) {

    var getAllChLists:LiveData<List<CheckListEntity>> = chListDao.getAllCheckLists()
    suspend fun saveNewCheckList(checkListEntity: CheckListEntity) = chListDao.saveNewCheckList(checkListEntity)

    suspend fun updateCurrentChList(checkListEntity: CheckListEntity) = chListDao.updateCurrentCheckList(checkListEntity)

    suspend fun deleteCurrentChList(checkListEntity: CheckListEntity) = chListDao.deleteSelectedCheckList(checkListEntity)

}