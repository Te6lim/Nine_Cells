package com.andyprojects.ninecells.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.andyprojects.ninecells.database.Player
import com.andyprojects.ninecells.user.PlayerFragmentDirections
import com.andyprojects.ninecells.user.UserViewModel

class MaxPlayersReachedDialog (
    private val act : Activity) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder (act)
            .setTitle ("Alert!")
            .setMessage ("Max players reached! delete a player")
            .setPositiveButton (android.R.string.ok) { _, _ ->
            }
            .create ()
    }
}