package com.eslammongy.helper.dao

import androidx.room.*
import com.eslammongy.helper.database.entities.CheckListEntity

@Dao
interface CheckListDao {

    @Query("SELECT * FROM CheckList_Table ORDER BY checkListId DESC")
    fun getAllCheckLists():List<CheckListEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNewCheckList(checkListEntity: CheckListEntity)

    @Update
    suspend fun updateCurrentCheckList(checkListEntity: CheckListEntity)

    @Delete
    suspend fun deleteSelectedCheckList(checkListEntity: CheckListEntity)
}