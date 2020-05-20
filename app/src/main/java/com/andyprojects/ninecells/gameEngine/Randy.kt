package com.andyprojects.ninecells.gameEngine

import com.andyprojects.ninecells.containsSomeElementsOf
import com.andyprojects.ninecells.gameScreen.GridViewModel
import java.util.*

class Randy private constructor (private val manager : GridManager , private val grid : List <Box>) {

    private val random = Random ()

    private var setOfPairsOfO = mutableListOf <MutableList <Int>> ()
    private var setOfPairsOfX = mutableListOf <MutableList <Int>> ()
    private var pairsMap = mutableListOf <List <Int>> ()

    private var pairKeyForO : Int = 0
    private var pairKeyForX : Int = 0

    private var newPair : MutableList <Int>? = null
    private var position = 0
    private var pairCount = 0
    private var pairMapSize = 0

    companion object {
        const val AGENT_NAME = "RANDY"
        var randy : Randy? = null
        fun getInstance (manager : GridManager, grid : List <Box>) : Randy {
            if (randy == null) {
                randy = Randy (manager , grid)
            }
            return randy!!
        }
    }

    fun onPlay () : Int {
        if (GridViewModel.playCount < 2) {
            onSetRandomPosition ()
        } else {
            if (manager.entriesForX.size > 1) {
                onCompletePair ()
            } else {
                onInterceptPlayer ()
            }
        }
        return position
    }

    private fun onCompletePair () {
        pairKeyForX = onFindExistingPair (setOfPairsOfX)
        if (pairKeyForX > 0) {
            if (!manager.entriesForO.contains (pairKeyForX) && !manager.entriesForX.contains (pairKeyForX)) {
                position = pairKeyForX
            } else {
                onInterceptPlayer ()
            }
        } else {
            onInterceptPlayer ()
        }
    }

    private fun onInterceptPlayer () {
        pairKeyForO = onFindExistingPair(setOfPairsOfO)
        if (pairKeyForO > 0) {
            if (!(manager.entriesForX.contains (pairKeyForO)) && !(manager.entriesForO.contains (pairKeyForO))) {
                position = pairKeyForO
                onFindNewPair ()
            } else {
                onTryToFollowPair ()
            }
        } else {
            onTryToFollowPair ()
        }
    }

    private fun onTryToFollowPair () {
        onFollowPair ()
        if (manager.entriesForO.contains (position) || manager.entriesForX.contains (position)) {
            onSetRandomPosition ()
        }
    }

    private fun onFollowPair () {
        if (pairCount < 3) {
            position = newPair!!.removeAt(random.nextInt (newPair!!.size))
        } else {
            onSetRandomPosition ()
        }
    }

    private fun onFindNewPair () {
        pairsMap = mutableListOf ()
        newPair = mutableListOf ()
        pairCount = 0
        pairsMap.addAll (grid [position - 1].setOfPaths)

        pairMapSize = pairsMap.size
        newPair!!.addAll (pairsMap [random.nextInt (pairMapSize)])
        for (items in pairsMap) {
            if (!manager.entriesForO.containsSomeElementsOf (items)) {
                newPair!!.addAll (items)
                return
            }
        }
    }

    private fun onSetRandomPosition () {
        position = random.nextInt (GridViewModel.gridSize) + 1
        while (manager.entriesForO.contains (position) || manager.entriesForX.contains (position)) {
            position = random.nextInt (GridViewModel.gridSize) + 1
        }
        onFindNewPair ()
    }

    private fun onFindExistingPair (setOfPairs : MutableList <MutableList <Int>>) : Int {
        var key = 0
        for (c in setOfPairs) {
            key = Pairs.get ().pairKey (c)
            if (key > 0) {
                return key
            }
        }
        return key
    }

    fun generateSetOfPairs (subSet : List <Int> , type : Char) {
        if (subSet.size >= 2) {
            if (type == manager.types [0]) {
                setOfPairsOfO = mutableListOf ()
                generatePair (subSet , setOfPairsOfO)
            } else {
                setOfPairsOfX = mutableListOf ()
                generatePair(subSet , setOfPairsOfX)
            }
        }
    }

    private fun generatePair (subSet : List <Int> , container : MutableList <MutableList <Int>>) {
        val lastValue = subSet.last ()
        var pair = mutableListOf <Int> ()
        for (c in 0..subSet.size -2) {
            pair.add (lastValue)
            pair.add (subSet [c])
            container.add (pair)
            pair = mutableListOf ()
        }
    }
}