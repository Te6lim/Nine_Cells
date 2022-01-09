package com.andyprojects.ninecells.user

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andyprojects.ninecells.database.PlayerDbDao

class UserViewModelFactory
    (private val dataSource : PlayerDbDao ,
     private val application : Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(dataSource, application) as T
        }
        throw IllegalAccessException("unknown view model class")
    }
}