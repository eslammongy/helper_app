package com.eslammongy.helper.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eslammongy.helper.database.dao.CheckListDao
import com.eslammongy.helper.database.dao.ContactDao
import com.eslammongy.helper.database.dao.TaskDao
import com.eslammongy.helper.database.entities.CheckListEntity
import com.eslammongy.helper.database.entities.ContactEntities
import com.eslammongy.helper.database.entities.TaskEntities

@Database(
    entities = [TaskEntities::class, ContactEntities::class, CheckListEntity::class],
    version = 1,
    exportSchema = false)
@TypeConverters(Converter::class)
abstract class HelperDataBase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun contactDao(): ContactDao
    abstract fun checkListDao(): CheckListDao

    companion object {

        var dataBaseInstance: HelperDataBase? = null

        @Synchronized
        fun getDataBaseInstance(context: Context): HelperDataBase {

            if (dataBaseInstance == null) {

                dataBaseInstance = Room.databaseBuilder(
                    context.applicationContext,
                    HelperDataBase::class.java,
                    "helper.db"
                ).allowMainThreadQueries().build()
            }
            return dataBaseInstance!!
        }
    }
}