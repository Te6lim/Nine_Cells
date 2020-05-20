package com.andyprojects.ninecells

fun List <Int>.itemsIdenticalTo (list : List <Int> , c : Int) : List <Int>? {
    val values = mutableListOf <Int> ()
    for (item in list) {
        if (this.contains (item)) {
            values.add(item)
        }
    }
    return if (values.size >= c) {
        values
    }else {
        null
    }
}

//You'll need to update this algorithm cos you know it sucks
fun List <Int>.belongsTo () : Int {
    return if ((this [0] % 2 == 0 && this [1] % 2 == 0)
        || (this[0] % 2 != 0 && this [1] % 2 != 0)) {
        (this [1] + this [0]) / 2
    } else if (this [0] == 1 && this [1] == 4 || this [0] == 8 && this [1] == 9) {
        7
    } else if (this [0] == 4 && this [1] == 7 || this [0] == 2 && this [1] == 3) {
        1
    } else if (this [0] == 3 && this [1] == 6 || this [0] == 7 && this [1] == 8) {
        9
    }else if (this [0] == 6 && this [1] == 9 || this [0] == 1 && this [1] == 2) {
        3
    } else {
        -1
    }
}

fun List <Int>.containsSomeElementsOf (list : List <Int>) : Boolean {
    for (i in list.indices) {
        if (this.contains (list [i])) {
            return true
        }
    }
    return false
}