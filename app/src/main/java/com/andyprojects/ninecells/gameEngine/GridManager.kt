package com.andyprojects.ninecells.gameEngine
import com.andyprojects.ninecells.itemsIdenticalTo

class GridManager (val types : List <Char> , private val grid : List <Box>) {


    lateinit var currentBox : Box
    var lastCheckedBox : Box? = null
    var entriesForO = mutableListOf <Int> ()
    var entriesForX  = mutableListOf <Int> ()

    var matchedItems = mutableListOf <Int> ()

    var aiAgent : Randy? = null

    fun inspect () : Boolean {
        matchedItems.clear ()
        val prioritySet = currentBox.prioritySet
        val setOfPairs = currentBox.setOfPaths
        val result : List <Int>?

        result = if (currentBox.type == types [0]) {
            prioritySet.itemsIdenticalTo (entriesForO  , 2)
        } else {
            prioritySet.itemsIdenticalTo(entriesForX ,2)
        }
        return areMatched (result , setOfPairs)
    }

    private fun areMatched (result : List <Int>? , maps : List < List<Int>>) : Boolean {
        if (result != null) {
            for ( c in maps.indices) {
                if (result.containsAll (maps [c])) {
                    matchedItems.addAll(maps [c])
                    return true
                }
            }
        }
        return false
    }

    fun setUpAiAgent () {
        aiAgent = Randy.getInstance(this , grid)
    }
}