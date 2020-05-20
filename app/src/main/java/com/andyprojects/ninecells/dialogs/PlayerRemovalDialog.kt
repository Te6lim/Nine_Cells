package com.andyprojects.ninecells.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.andyprojects.ninecells.R
import com.andyprojects.ninecells.database.Player
import com.andyprojects.ninecells.user.UserViewModel

class PlayerRemovalDialog (
    private  val act : Activity , private val userViewModel : UserViewModel,
    private val player : Player
) : DialogFragment () {
    override fun onCreateDialog (savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder (act)
            .setTitle ("Alert")
            .setMessage (getString (R.string.player_removal_message , player.name))
            .setPositiveButton (android.R.string.ok) { _, _ ->
                userViewModel.removePlayer (player)
            }
            .setNegativeButton(android.R.string.cancel) {_, _ ->

            }
            .create ()

    }
}