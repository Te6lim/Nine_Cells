package com.andyprojects.ninecells.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlayerDbDao {
    @Insert
    fun insert (player : Player)

    @Update
    fun update (player : Player)

    @Query ("SELECT * from player_data_table WHERE playerId = :key")
    fun getPlayer (key : Long) : Player?

    @Query ("DELETE from player_data_table")
    fun clear ()

    @Query ("SELECT * from player_data_table ORDER BY high_score DESC")
    fun getAllPlayers () : LiveData <List <Player>>

    @Query ("SELECT * from player_data_table ORDER BY playerId LIMIT 1")
    fun getCurrentPlayer () : Player?

    @Query ("DELETE from player_data_table WHERE playerId = :key")
    fun removePlayer (key : Long)
}