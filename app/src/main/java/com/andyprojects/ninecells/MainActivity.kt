package com.andyprojects.ninecells

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.andyprojects.ninecells.database.Player
import com.andyprojects.ninecells.database.PlayerDb
import com.andyprojects.ninecells.interfaces.ActivityFragmentInterface
import com.andyprojects.ninecells.user.UserViewModel
import com.andyprojects.ninecells.user.UserViewModelFactory

class MainActivity : AppCompatActivity() , ActivityFragmentInterface {

    lateinit var userViewModel : UserViewModel
    lateinit var playerList : List <Player>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(this , R.id.myNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this , navController)

        val dataSource = PlayerDb.getInstance(this).playerDbDao
        val application = this.application
        val userViewModelFactory = UserViewModelFactory (dataSource , application)
        userViewModel = ViewModelProviders
            .of (this , userViewModelFactory).get (UserViewModel :: class.java)

        userViewModel.players.observe(this , Observer {
            playerList = it
        })
    }

    override fun onSupportNavigateUp () : Boolean {
        val navController = findNavController (this , R.id.myNavHostFragment)
        return navController.navigateUp ()
    }

    override fun getUserVewModel() : UserViewModel = userViewModel
}