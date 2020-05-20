package com.andyprojects.ninecells.gameScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andyprojects.ninecells.user.UserViewModel

class GridViewModelFactory (private var userViewModel : UserViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom (GridViewModel :: class.java)) {
            return GridViewModel (userViewModel) as T
        }
        throw IllegalAccessException ("Unknown view model")
    }

}