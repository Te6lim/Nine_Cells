package com.andyprojects.ninecells.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "player_data_table")
data class Player(

    @ColumnInfo (name = "name")
    var name : String ,

    @PrimaryKey(autoGenerate = true)
     var playerId : Long = 0L

    ) {

    @ColumnInfo (name = "score")
    var score : Int = 0

    @ColumnInfo (name = "character")
    var character : Char = 'O'

    @ColumnInfo (name = "high_score")
    var highScore : Int = 0

    @ColumnInfo (name = "high_score_time")
    var highTime : Long = 0L
}