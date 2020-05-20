package com.andyprojects.ninecells.user

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andyprojects.ninecells.R
import com.andyprojects.ninecells.database.Player
import com.andyprojects.ninecells.databinding.FragmentPlayerBinding
import com.andyprojects.ninecells.databinding.ItemPlayerBinding
import com.andyprojects.ninecells.dialogs.MaxPlayersReachedDialog
import com.andyprojects.ninecells.dialogs.PlayerRemovalDialog
import com.andyprojects.ninecells.dialogs.PlayerConfirmationDialog
import com.andyprojects.ninecells.interfaces.ActivityFragmentInterface

class PlayerFragment : Fragment() {

    private val dialogTag : String = "CONFIRM"

    private var afi : ActivityFragmentInterface? = null

    private var playerAdapter : PlayerAdapter? = null
    private var recyclerView : RecyclerView? = null

    private var playerList : List <Player>? = null

    private lateinit var userViewModel : UserViewModel

    private lateinit var binding : FragmentPlayerBinding

    private var currentPlayer : Player? = null
    private var currentPlayerB : Player? = null

    var nameA : String? = null ; var nameB : String? = null

    override fun onAttach (context : Context) {
        super.onAttach (context)
        afi =  activity as ActivityFragmentInterface
    }

    override fun onDetach () {
        super.onDetach ()
        afi = null
    }

    var menu : Menu? = null
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate (R.menu.fragment_player , menu)
        this.menu = menu
    }

    override fun onOptionsItemSelected (menuItem : MenuItem) : Boolean {
        return when (menuItem.itemId) {
            R.id.add_new_player -> {
                showPlayerInfoView ()
                menuItem.isVisible = false
                menu!!.findItem (R.id.list_item).isVisible = true
                true
            }
            R.id.list_item -> {
                showPlayerList ()
                menuItem.isVisible = false
                menu!!.findItem (R.id.add_new_player).isVisible = true
                true
            }
            else -> super.onOptionsItemSelected (menuItem)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerAdapter = PlayerAdapter (ItemOnClickListener ({
            findNavController ()
                .navigate (PlayerFragmentDirections
                    .actionPlayerFragmentToGameFragment(
                        it , null
                    ))
        } , {
            PlayerRemovalDialog (requireActivity() , userViewModel , it)
                .show (parentFragmentManager , "REMOVE")
        }))
    }

    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?) : View? {
        userViewModel = afi!!.getUserVewModel ()
        setHasOptionsMenu (true)

        binding = DataBindingUtil.inflate (inflater , R.layout.fragment_player , container , false)
        binding.lifecycleOwner = this

        recyclerView = binding.playersRecyclerView
        recyclerView?.adapter = playerAdapter
        recyclerView?.layoutManager = LinearLayoutManager (context)

        userViewModel.players.observe (viewLifecycleOwner , Observer {
            playerList = it
            if (it.isNotEmpty ()) {
                showPlayerList ()
                playerAdapter!!.players = playerList!!
            } else {
                showPlayerInfoView ()
            }
        })

        return binding.root
    }

    private fun showMultiPlayerView () {
        binding.nameSpace.hint = getString(R.string.player_a)
        binding.versusText.visibility = View.VISIBLE
        binding.playerB.visibility = View.VISIBLE
        binding.enterButton.isEnabled = false

        if (binding.nameSpace.text.isNotEmpty () && binding.playerB.text.isNotEmpty ()) {
            nameA = binding.nameSpace.text.toString ()
            nameB = binding.playerB.text.toString ()
            binding.enterButton.isEnabled = true
        }

        binding.nameSpace.addTextChangedListener ( object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                nameA = s.toString ()
                binding.enterButton.isEnabled = s.toString ().isNotEmpty () && nameB != null
            }

        })


        binding.playerB.addTextChangedListener ( object : TextWatcher {
            override fun afterTextChanged (s: Editable?) {

            }

            override fun beforeTextChanged (s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged (s: CharSequence?, start: Int, before: Int, count: Int) {
                nameB = s.toString ()
                binding.enterButton.isEnabled = s.toString ().isNotEmpty () && nameB != null
            }

        })
    }

    private fun showSinglePlayerView () {
        binding.nameSpace.hint = getString(R.string.what_is_your_name)
        binding.versusText.visibility = View.GONE
        binding.playerB.visibility = View.GONE
        binding.enterButton.isEnabled = false
        nameB = null

        if (binding.nameSpace.text.isNotEmpty ()) {
            nameA = binding.nameSpace.text.toString ()
            binding.enterButton.isEnabled = true
        }

        binding.nameSpace.addTextChangedListener ( object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                nameA = s.toString ()
                binding.enterButton.isEnabled = s.toString().isNotEmpty()
            }

        })
    }

    private fun showPlayerInfoView () {
        binding.playersRecyclerView.visibility = View.GONE
        binding.playerInfoView.visibility = View.VISIBLE
        binding.enterButton.isEnabled = false

        binding.playOptions.clearCheck ()

        binding.playOptions
            .setOnCheckedChangeListener { _, checkedId ->  when (checkedId) {
                R.id.multi_player_option -> showMultiPlayerView ()
                R.id.single_player_option -> showSinglePlayerView ()
                else -> showSinglePlayerView ()
            } }

        binding.enterButton.setOnClickListener {
            val maxDialogTag = "MAX"
            if (nameA!!.isNotEmpty () && nameB == null) {
                if (nameA!!.length < UserViewModel.MAX_NAME_LENGTH) {
                    if (userViewModel.playerDoesNotExists (nameA!!)) {
                        currentPlayer = Player (nameA!!)
                        if (playerList != null && playerList!!.size >= 10) {
                            MaxPlayersReachedDialog (requireActivity ()).show (parentFragmentManager , maxDialogTag)
                        } else {
                            userViewModel.addNewPlayer (currentPlayer!!)
                            findNavController ().navigate (PlayerFragmentDirections
                                .actionPlayerFragmentToGameFragment(nameA!! , null))
                        }
                    } else {
                        PlayerConfirmationDialog(
                            requireActivity(),
                            nameA!!,
                            null
                        ).show (parentFragmentManager , dialogTag)
                    }
                } else {
                    Toast.makeText (requireContext() , R.string.name_too_long , Toast.LENGTH_SHORT). show ()
                }
            } else {
                if (nameA!!.isNotEmpty() && nameB!!.isNotEmpty()) {
                    if (nameA!!.length < UserViewModel.MAX_NAME_LENGTH && nameB!!.length < UserViewModel.MAX_NAME_LENGTH) {
                        if (userViewModel.playerDoesNotExists (nameB!!) && userViewModel.playerDoesNotExists(nameA!!)) {
                            currentPlayerB = Player (nameB!!)
                            currentPlayer = Player (nameA!!)

                            if (playerList != null && playerList!!.size >= 10) {
                                MaxPlayersReachedDialog (requireActivity ()).show (parentFragmentManager , maxDialogTag)
                            } else {
                                if (playerList!!.size <= 8) {
                                    userViewModel.apply {
                                        addNewPlayer (currentPlayer!!)
                                        addNewPlayer (currentPlayerB!!)
                                    }
                                    findNavController ().navigate (PlayerFragmentDirections
                                        .actionPlayerFragmentToGameFragment(nameA!! , nameB))
                                } else {
                                    MaxPlayersReachedDialog (requireActivity ()).show (parentFragmentManager , maxDialogTag)
                                }
                            }
                        } else {
                            if (userViewModel.playerDoesNotExists(nameB!!) && !userViewModel.playerDoesNotExists(nameA!!)) {
                                currentPlayerB = Player (nameB!!)
                                currentPlayer = userViewModel.getPlayer (nameA!!)
                                if (playerList!!.size >= UserViewModel.MAX_DB_SIZE) {
                                    MaxPlayersReachedDialog (requireActivity ()).show (parentFragmentManager , maxDialogTag)
                                } else {
                                    userViewModel.addNewPlayer (currentPlayerB!!)
                                    findNavController ().navigate (PlayerFragmentDirections
                                        .actionPlayerFragmentToGameFragment(nameA!! , nameB))
                                }
                            } else {
                                if (!userViewModel.playerDoesNotExists(nameB!!) && userViewModel.playerDoesNotExists(nameA!!)) {
                                    currentPlayer = Player (nameA!!)
                                    currentPlayerB = userViewModel.getPlayer (nameB!!)
                                    if (playerList!!.size >= UserViewModel.MAX_DB_SIZE) {
                                        MaxPlayersReachedDialog (requireActivity ()).show (parentFragmentManager , maxDialogTag)
                                    } else {
                                        userViewModel.addNewPlayer (currentPlayer!!)
                                        findNavController ().navigate (PlayerFragmentDirections
                                            .actionPlayerFragmentToGameFragment(nameA!! , nameB))
                                    }
                                } else {
                                    PlayerConfirmationDialog(
                                        requireActivity(),
                                        nameA!!,
                                        nameB
                                    ).show (parentFragmentManager , "CONFIRM")
                                }
                            }
                        }
                    } else {
                        Toast.makeText (requireContext () , R.string.some_name_too_long , Toast.LENGTH_SHORT).show ()
                    }
                }
            }
        }
    }

    private fun showPlayerList () {
        binding.playersRecyclerView.visibility = View.VISIBLE
        binding.playerInfoView.visibility = View.GONE
    }

    inner class PlayerAdapter (private val itemOnClickListener : ItemOnClickListener) :
        RecyclerView.Adapter <PlayerAdapter.PlayerViewHolder> () {
        var players = listOf <Player> ()
            set (value) {
                field = value
                notifyDataSetChanged ()
            }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : PlayerViewHolder {
            val inflater : LayoutInflater = LayoutInflater.from (context)
            val playerBinding : ItemPlayerBinding =
                DataBindingUtil.inflate (inflater , R.layout.item_player , parent , false)
            return PlayerViewHolder (playerBinding)
        }

        override fun getItemCount(): Int {
            return players.size
        }

        override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
            holder.bind (players [position] , itemOnClickListener)
        }

        inner class PlayerViewHolder (
            private val playerBinding : ItemPlayerBinding) : RecyclerView.ViewHolder (playerBinding.root) {

            fun bind (
                player: Player,
                itemOnClickListener: ItemOnClickListener
            ) {
                playerBinding.player = player
                playerBinding.itemOnClickListener = itemOnClickListener
            }
        }
    }
}

class ItemOnClickListener (val navigateBlock : (String) -> Unit , val removeBlock : (Player) -> Unit?) {
    fun onClick (player : Player) {
        navigateBlock (player.name)
    }
    fun onRemoveItemClicked (player : Player) {
        removeBlock (player)
    }
}