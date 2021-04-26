package com.eslammongy.helper.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.eslammongy.helper.database.entities.CheckListEntity
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.database.entities.TaskEntities


@Database(entities = [TaskEntities::class , ContactEntities::class , CheckListEntity::class] , version = 1 , exportSchema = false)
abstract class HelperDataBase:RoomDatabase(){

    companion object{

        var dataBaseInstance:HelperDataBase? = null

        fun getDataBaseInstance(context: Context):HelperDataBase{

            if (dataBaseInstance == null){

                dataBaseInstance = Room.databaseBuilder(context.applicationContext ,
                HelperDataBase::class.java ,
                "helper.db").allowMainThreadQueries().build()
            }
            return dataBaseInstance!!
        }
    }
}