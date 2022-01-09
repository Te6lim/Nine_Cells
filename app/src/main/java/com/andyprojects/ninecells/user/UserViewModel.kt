package com.andyprojects.ninecells.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.andyprojects.ninecells.database.Player
import com.andyprojects.ninecells.database.PlayerDbDao
import com.andyprojects.ninecells.gameEngine.GridManager
import com.andyprojects.ninecells.gameScreen.GridViewModel
import kotlinx.coroutines.*
import java.util.*

class UserViewModel(
    private val dataSource: PlayerDbDao, application: Application
) : AndroidViewModel(application) {

    companion object {
        const val MAX_NAME_LENGTH = 7
        const val MAX_DB_SIZE = 10
    }

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    var player = MutableLiveData<Player?>()
    var opponent = MutableLiveData<Player?>()

    val players = dataSource.getAllPlayers()

    var firstPlayerScore = MutableLiveData(0)
    var opponentScore = MutableLiveData(0)

    fun addNewPlayer(p: Player) {
        uiScope.launch {
            addPlayerToDatabase(p)
        }
    }

    private suspend fun addPlayerToDatabase(p: Player) {
        return withContext(Dispatchers.IO) {
            dataSource.insert(p)
        }
    }

    fun removePlayer(p: Player) {
        uiScope.launch {
            removePlayerFromDatabase(p)
        }
    }

    private suspend fun removePlayerFromDatabase(p: Player) {
        return withContext(Dispatchers.IO) { dataSource.removePlayer(p.playerId) }
    }

    private fun updatePlayer(p: Player) {
        uiScope.launch {
            updatePlayerInDatabase(p)
        }
    }

    private suspend fun updatePlayerInDatabase(p: Player) {
        return withContext(Dispatchers.IO) {
            dataSource.update(p)
        }
    }

    fun getPlayer(name: String): Player? {
        for (c in players.value!!) {
            if (c.name == name) {
                return c
            }
        }
        return null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun setFirstPlayerHighScore(name: String) {
        var p: Player? = null
        for (c in players.value!!) {
            if (c.name.equals(name, ignoreCase = true)) {
                p = c
            }
        }
        player.value = p
        if (player.value!!.highScore < 1) {
            player.value!!.highScore = firstPlayerScore.value!!
            updatePlayer(player.value!!)
        } else {
            if (player.value!!.highScore < firstPlayerScore.value!! && firstPlayerScore.value!! > opponentScore.value!!) {
                player.value!!.highScore = firstPlayerScore.value!!
                updatePlayer(player.value!!)
            }
        }
    }

    fun setOpponentHighScore(nameB: String?) {
        var p: Player? = null
        for (c in players.value!!) {
            if (c.name.toLowerCase(Locale.ROOT) == nameB?.toLowerCase(Locale.ROOT)) {
                p = c
            }
        }
        opponent.value = p
        if (opponent.value!!.highScore < 1) {
            opponent.value!!.highScore = opponentScore.value!!
            if (opponent.value != null) {
                updatePlayer(opponent.value!!)
            }
        } else {
            if (opponent.value!!.highScore < opponentScore.value!! && opponentScore.value!! > firstPlayerScore.value!!) {
                opponent.value!!.highScore = opponentScore.value!!
                if (opponent.value != null) {
                    updatePlayer(opponent.value!!)
                }
            }
        }
    }

    fun playerDoesNotExists(name: String): Boolean {
        for (c in players.value!!) {
            if (c.name.toLowerCase(Locale.ROOT) == name.lowercase(Locale.ROOT)) {
                return false
            }
        }
        return true
    }

    fun setScore(name: String, nameB: String?) {
        if (GridManager.get()!!.currentBox.type == GridViewModel.userType) {
            firstPlayerScore.value = firstPlayerScore.value?.plus(1)
            if (opponentScore.value!! > 0) {
                opponentScore.value = opponentScore.value?.minus(1)
            }
            setFirstPlayerHighScore(name)
        } else {
            opponentScore.value = opponentScore.value?.plus(1)
            if (firstPlayerScore.value!! > 0) {
                firstPlayerScore.value = firstPlayerScore.value?.minus(1)
            }
            if (nameB != null) {
                setOpponentHighScore(nameB)
            }
        }
    }
}