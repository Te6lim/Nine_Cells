package com.andyprojects.ninecells.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.andyprojects.ninecells.R
import com.andyprojects.ninecells.user.PlayerFragmentDirections
import java.util.*

class PlayerConfirmationDialog (private val cont : Activity, private val nameA : String, private val nameB : String?) : DialogFragment() {
    private lateinit var stringResource : String
    @ExperimentalStdlibApi
    override fun onCreateDialog (savedInstanceState : Bundle?) : Dialog {
        stringResource = if (nameB == null) {
            getString (R.string.player_confirmation_message, nameA.capitalize(Locale.ROOT))
        } else {
            getString (R.string.players_confirmation_message , nameA.capitalize(Locale.ROOT), nameB.capitalize(Locale.ROOT))
        }
        return AlertDialog.Builder (cont)
            .setTitle (R.string.confirmation)
            .setMessage(stringResource)
            .setNegativeButton(android.R.string.cancel) { _, _ ->
            }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                if (nameB == null) {
                    findNavController ().navigate (
                        PlayerFragmentDirections.actionPlayerFragmentToGameFragment(
                            nameA,
                            null
                        )
                    )
                } else {
                    findNavController ().navigate (
                        PlayerFragmentDirections.actionPlayerFragmentToGameFragment(
                            nameA,
                            nameB
                        )
                    )
                }
            }
            .create ()
    }
}