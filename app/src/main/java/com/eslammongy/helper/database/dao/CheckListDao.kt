package com.eslammongy.helper.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.eslammongy.helper.database.entities.CheckListEntity
import com.eslammongy.helper.database.entities.SubCheckList

@Dao
interface CheckListDao {

    @Query("SELECT * FROM CheckList_Table ORDER BY checkListId DESC")
    suspend fun getAllCheckLists():List<CheckListEntity>

    @Query("SELECT * FROM SubChl_Table WHERE parentChListId = :chlID")
    suspend fun getAllSubCheckLists(chlID:Int):List<SubCheckList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNewCheckList(checkListEntity: CheckListEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNewSubCheckList(subCheckList: SubCheckList)

    @Update
    suspend fun updateCurrentCheckList(checkListEntity: CheckListEntity)

     @Query("UPDATE checklist_table SET checkList_Completed = :isComplete WHERE checkListId = :id")
     fun getCompleteStatus(id:Int , isComplete:Boolean)

    @Query("UPDATE SubChl_Table SET subChl_Completed = :isComplete WHERE subCheckLId = :id")
     fun getSubChlCompleteStatus(id:Int , isComplete:Boolean)

    @Delete
    suspend fun deleteSelectedCheckList(checkListEntity: CheckListEntity)

    @Delete
    suspend fun deleteSelectedSubCheckList(subCheckList: SubCheckList)

    @Query("SELECT * FROM checklist_table WHERE checkList_Title LIKE :searchQuery")
    suspend fun searchInCheckListDataBase(searchQuery: String): List<CheckListEntity>
}