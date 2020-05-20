package com.andyprojects.ninecells.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database (entities = [Player :: class] , version = 1 , exportSchema = false)
abstract class PlayerDb : RoomDatabase () {

    abstract val playerDbDao : PlayerDbDao

    companion object {
        @Volatile
        private var INSTANCE : PlayerDb? = null

        fun getInstance (context : Context) : PlayerDb {
            synchronized (this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder (context.applicationContext ,
                        PlayerDb :: class.java , "player-database_history")
                        .fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}