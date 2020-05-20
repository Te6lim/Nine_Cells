package com.andyprojects.ninecells.user

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.andyprojects.ninecells.R
import com.andyprojects.ninecells.database.Player


@BindingAdapter ("playerName")
fun TextView.setPlayerName (item : Player?) {
    item?.let {
        text = resources.getString (R.string.name_holder , item.name)
    }
}

@BindingAdapter ("playerHighScore")
fun TextView.setPlayerHighScore (item : Player?) {
    item?.let {
        text = resources.getString (R.string.high_score_holder , item.highScore)
    }
}