package com.andyprojects.ninecells.gameScreen
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andyprojects.ninecells.R
import com.andyprojects.ninecells.gameEngine.Box
import com.andyprojects.ninecells.gameEngine.GridManager
import com.andyprojects.ninecells.gameEngine.Randy
import com.andyprojects.ninecells.user.UserViewModel

class GridViewModel (val userViewModel : UserViewModel) : ViewModel() {

    companion object {
        var playCount : Int = 0
        private set
        const val gridSize : Int = 9
        val types :List <Char> = listOf ('O' , 'X')
    }

    var manager : GridManager
    private var grid  = mutableListOf <Box> ()
    private var lastSelectedBox : Box? = null
    private var currentlySelectedBox : Box? = null

    private var userType : Char = types [0]
    var typeSwitch = 0
        private set

    var moveComplete = MutableLiveData <Boolean> ()
    var moveMade = MutableLiveData <Boolean> (false)

    init {
        for (c : Int in 1..gridSize) {
            grid.add (Box (c))
        }
        manager = GridManager (types , grid.toList ())
        manager.setUpAiAgent ()
    }

    fun onBoxSelected (position : Int) : Int {
        ++ playCount
        currentlySelectedBox = grid [position]
        manager.currentBox = currentlySelectedBox!!
        setBoxType ()
        lastSelectedBox = currentlySelectedBox
        manager.lastCheckedBox = lastSelectedBox
        return getItemResourceId ()
    }

    private fun setBoxType() {
        if (lastSelectedBox == null) {
            currentlySelectedBox!!.type = types [typeSwitch]
            lastSelectedBox = currentlySelectedBox
        } else {
            if (lastSelectedBox!!.type == types [0]) {
                currentlySelectedBox!!.type = types [1]
            } else {
                currentlySelectedBox!!.type = types [0]
            }
        }
    }

    private fun getItemResourceId () : Int = when (currentlySelectedBox!!.type) {
        types [0] -> R.drawable.ic_o
        else -> R.drawable.ic_x
    }

    /** gets the pair that matches with the current position, it such pair doesnt exist, returns null**/
    fun doSomeMagic (index : Int) : List <Int>? {
        if (playCount > 4) {
            if (manager.inspect ()) {
                return manager.matchedItems
            }
        }
        splitEntries (index)
        return null
    }

    private fun splitEntries (index : Int) {
        val position = index + 1
        when (grid [index].type) {
            types [0] -> {
                manager.entriesForO.add (position)
                if (manager.entriesForO.size >= 2) {
                    manager.aiAgent?.generateSetOfPairs (manager.entriesForO , types [0])
                }
            }
            types [1] -> {
                manager.entriesForX.add (position)
                if (manager.entriesForX.size >= 2) {
                    manager.aiAgent?.generateSetOfPairs (manager.entriesForX , types [1])
                }
            }
        }
    }

    fun resetFields () {
        playCount = 0
        currentlySelectedBox = null
        lastSelectedBox = null
        Randy.randy = null
        manager = GridManager (types , grid.toList ())
    }

    fun switchUser () {
        moveComplete.value = if (typeSwitch == 0) {
            ++ typeSwitch
            true
        } else {
            -- typeSwitch
            false
        }
    }

    fun resetSwitch () {
        if (typeSwitch > 0) {
            --typeSwitch
            moveComplete.value = false
        }
    }

    fun playerAndAiSwitch (matched : List <Int>?) {
        if (playCount < 9 && matched == null) {
            if (!moveMade.value!!) {
                moveMade.value = true
            }
        }
    }

    fun setScore (name : String , nameB : String?) {
        if (manager.currentBox.type == userType) {
            userViewModel.firstPlayerScore.value = userViewModel.firstPlayerScore.value?.plus (1)
            if (userViewModel.opponentScore.value!! > 0) {
                userViewModel.opponentScore.value = userViewModel.opponentScore.value?.minus (1)
            }
            userViewModel.setFirstPlayerHighScore (name)
        } else {
            userViewModel.opponentScore.value = userViewModel.opponentScore.value?.plus (1)
            if (userViewModel.firstPlayerScore.value!! > 0) {
                userViewModel.firstPlayerScore.value = userViewModel.firstPlayerScore.value?.minus (1)
            }
            if (nameB != null) {
                userViewModel.setOpponentHighScore (nameB)
            }
        }
    }
}