package com.andyprojects.ninecells.gameScreen

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andyprojects.ninecells.R
import com.andyprojects.ninecells.database.PlayerDb
import com.andyprojects.ninecells.databinding.FragmentGameBinding
import com.andyprojects.ninecells.databinding.ItemViewBinding
import com.andyprojects.ninecells.gameEngine.Randy
import com.andyprojects.ninecells.user.UserViewModel
import com.andyprojects.ninecells.user.UserViewModelFactory

class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding
    private lateinit var viewModel: GridViewModel
    private lateinit var userViewModel: UserViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var restartButton: Button
    private lateinit var endButton: Button
    private lateinit var turnIndicator: ImageView

    private lateinit var firstPlayerView: ConstraintLayout
    private lateinit var opponentView: ConstraintLayout

    private var humanOpponentName: String? = null
    private lateinit var playerName: String

    private var adapter: GameScreenAdapter? = null

    private val itemViews = mutableListOf<ItemViewBinding>()

    override fun onDetach() {
        super.onDetach()
        viewModel.apply {
            resetFields()
            resetSwitch()
            userViewModel.apply {
                firstPlayerScore.value = 0
                opponentScore.value = 0
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_game, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController()) ||
                super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?
    ): View {
        setHasOptionsMenu(true)
        viewModel = ViewModelProvider(this)[GridViewModel::class.java]

        val dataSource = PlayerDb.getInstance(requireContext()).playerDbDao
        val application = requireActivity().application
        val userViewModelFactory = UserViewModelFactory(dataSource, application)
        userViewModel = ViewModelProvider(
            this, userViewModelFactory
        )[UserViewModel::class.java]

        playerName = GameFragmentArgs.fromBundle(requireArguments()).name
        humanOpponentName = GameFragmentArgs.fromBundle(requireArguments()).nameB
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_game, container, false)
        binding.gameView.setBackgroundResource(R.color.gridBackGroundColor)
        recyclerView = binding.recyclerView

        firstPlayerView = binding.firstPlayerLayout
        opponentView = binding.opponentLayout

        if (humanOpponentName == null) {
            viewModel.manager.setUpAiAgent()
            binding.player1Text.text = playerName
            binding.player2Text.setTextColor(resources.getColor(R.color.aiAgentTextColor))
            binding.player2Text.text = Randy.AGENT_NAME
        } else {
            binding.player1Text.text = GameFragmentArgs.fromBundle(requireArguments()).name
            binding.player2Text.text = humanOpponentName
        }

        updateUi()
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        binding.userViewModel = userViewModel
        binding.lifecycleOwner = this

        turnIndicator = binding.turnIndicator
        indicateTurn()

        restartButton = binding.restartButton
        restartButton.isEnabled = false

        restartButton.setOnClickListener {
            adapter = null
            updateUi()
            viewModel.apply {
                resetFields()
                switchUser()
                manager.setUpAiAgent()
                indicateTurn()
            }
            itemViews.clear()
            viewModel.moveMade.value = false
            restartButton.isEnabled = false
            endButton.isEnabled = false
        }

        endButton = binding.endButton
        endButton.isEnabled = false
        endButton.setOnClickListener {
            findNavController().navigate(R.id.action_gameFragment_to_playerFragment)
        }

        if (humanOpponentName == null) {
            viewModel.moveMade.observe(viewLifecycleOwner, Observer {
                if (it) {
                    val position = viewModel.manager.aiAgent!!.onPlay()
                    onAIClick(position)
                }
            })
        }

        return binding.root
    }

    private fun indicateTurn() {
        if (viewModel.typeSwitch < 1) {
            turnIndicator.setImageResource(R.mipmap.ic_o_non_transparent)
            firstPlayerView.setBackgroundResource(R.color.colorAccent)
            binding.player1Text.setTextColor(resources.getColor(R.color.gridBackGroundColor))
            opponentView.setBackgroundResource(R.color.gridBackGroundColor)
            binding.player2Text.setTextColor(resources.getColor(R.color.aiAgentTextColor))
        } else {
            turnIndicator.setImageResource(R.mipmap.ic_x_non_transparent)
            firstPlayerView.setBackgroundResource(R.color.gridBackGroundColor)
            binding.player2Text.setTextColor(resources.getColor(R.color.gridBackGroundColor))
            opponentView.setBackgroundResource(R.color.colorAccent)
            binding.player1Text.setTextColor(resources.getColor(R.color.playerNameColor))
        }
    }

    private fun onAIClick(position: Int) {
        itemViews[position - 1].root.callOnClick()
        viewModel.moveMade.value = false
    }

    inner class GameScreenAdapter : RecyclerView.Adapter<GameScreenAdapter.BoxViewHolder>() {

        override fun getItemCount(): Int {
            return GridViewModel.gridSize
        }

        override fun onBindViewHolder(viewHolder: BoxViewHolder, position: Int) {
            when (position) {
                8 -> if (viewModel.moveComplete.value == true) {
                    viewModel.moveMade.value = true
                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoxViewHolder {
            val inflater: LayoutInflater = LayoutInflater.from(context)
            val binding: ItemViewBinding =
                DataBindingUtil.inflate(inflater, R.layout.item_view, parent, false)
            itemViews.add(binding)
            return BoxViewHolder(binding)
        }

        inner class BoxViewHolder
            (private val itemBinding: ItemViewBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {

            init {
                var matched: List<Int>?

                itemView.setOnClickListener {
                    itemBinding.itemButton.apply {
                        setImageResource(viewModel.onBoxSelected(absoluteAdapterPosition))
                        isEnabled = false
                        if (GridViewModel.playCount == 9) {
                            restartButton.isEnabled = true
                            endButton.isEnabled = true
                        }
                    }
                    matched = viewModel.doSomeMagic(adapterPosition)
                    if (matched != null) {
                        setColorForMatched(matched!!)
                        restartButton.isEnabled = true
                        endButton.isEnabled = true
                    }
                    viewModel.playerAndAiSwitch(matched)
                }
            }

            private fun setColorForMatched(matched: List<Int>) {
                itemView.setBackgroundResource(R.color.colorAccent)
                for (c: Int in matched.indices) {
                    itemViews[(matched[c] - 1)].root.setBackgroundResource(R.color.colorAccent)
                }
                userViewModel.setScore(playerName, humanOpponentName)
                disableUnchecked()
            }

            private fun disableUnchecked() {
                for (c in itemViews) {
                    if (c.root.isEnabled) {
                        c.root.isEnabled = false
                    }
                }
            }
        }
    }

    private fun updateUi() {
        if (adapter == null) {
            adapter = GameScreenAdapter()
            recyclerView.adapter = adapter
        }
    }
}