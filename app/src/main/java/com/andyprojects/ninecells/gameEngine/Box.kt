package com.andyprojects.ninecells.gameEngine

class Box (private var position : Int) {
    var type : Char? = null
    val prioritySet : List <Int>
    private val pairs : Pairs = Pairs.get ()
    val setOfPaths : List <List < Int>> = pairs of position

    init {
        prioritySet = prioritySet ()
    }

    private fun prioritySet () : List <Int>  = when (position) {
        1 -> listOf (2 , 3 , 4 , 5 , 7 , 9)
        2 -> listOf (1 , 3 , 5 , 8)
        3 -> listOf (1 , 2 , 5 , 6 , 7 , 9)
        4 -> listOf (1 , 5 , 6 , 7)
        5 -> listOf (1 , 2 , 3 , 4 , 6 , 7 , 8 , 9)
        6 -> listOf (3 , 4 , 5 , 9)
        7 -> listOf (1 , 3 , 4 , 5 , 8 , 9)
        8 -> listOf (2 , 5 , 7 , 9)
        else -> listOf (1 , 3 , 5 , 6 , 7 , 8)
    }
}