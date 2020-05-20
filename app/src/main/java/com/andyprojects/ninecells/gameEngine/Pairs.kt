package com.andyprojects.ninecells.gameEngine

class Pairs private constructor() {
    private var pairsCollections = mutableListOf <List <List <Int>>> ()

    companion object {
        private var instance : Pairs? = null
        fun get () : Pairs {
            if (instance == null) {
                instance = Pairs ()
            }
            return instance!!
        }
    }

    init {
        pairsCollections.apply {
            add (listOf (listOf (2 , 3) , listOf (4 , 7) , listOf (5 , 9)))
            add (listOf (listOf (1 , 3) , listOf (5 , 8)))
            add (listOf (listOf (1 , 2) , listOf (5 , 7) , listOf (6 , 9)))
            add (listOf (listOf (1 , 7) , listOf (5 , 6)))
            add (listOf (listOf (1 ,9) , listOf (2 , 8) , listOf (3 , 7) , listOf (4 , 6)))
            add (listOf (listOf (3 , 9) , listOf (4 , 5)))
            add (listOf (listOf (1 , 4) , listOf (3 , 5) , listOf (8 , 9)))
            add (listOf (listOf (2 , 5) , listOf (7 , 9)))
            add (listOf (listOf (1 , 5) , listOf (3 , 6) , listOf (7 , 8)))
        }
    }

    infix fun of (owner : Int) = pairsCollections [owner - 1]

    fun pairKey (path : List <Int>) : Int {
        var pairsCollection : List <List <Int>>
        var pairs : List <Int>
        for (c in pairsCollections.indices) {
            pairsCollection = pairsCollections [c]
            for (k in pairsCollection.indices) {
                pairs = pairsCollection [k]
                if (pairs.containsAll (path)) {
                    return c+1
                }
            }
        }
        return 0
    }
}