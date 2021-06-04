package com.eslammongy.helper.database.dao

import androidx.room.*
import com.eslammongy.helper.database.entities.CheckListEntity
import com.eslammongy.helper.database.entities.SubCheckList

@Dao
interface CheckListDao {

    @Query("SELECT * FROM CheckList_Table ORDER BY checkListId DESC")
    fun getAllCheckLists():List<CheckListEntity>

    //@Transaction
    @Query("SELECT * FROM SubChl_Table WHERE parentChListId = :chlID")
    fun getAllSubCheckLists(chlID:Int):List<SubCheckList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun saveNewCheckList(checkListEntity: CheckListEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveNewSubCheckList(subCheckList: SubCheckList)

    @Update
     fun updateCurrentCheckList(checkListEntity: CheckListEntity)

     @Query("UPDATE checklist_table SET checkList_Completed = :isComplete WHERE checkListId = :id")
     fun getCompleteStatus(id:Int , isComplete:Boolean)


    @Delete
     fun deleteSelectedCheckList(checkListEntity: CheckListEntity)
}