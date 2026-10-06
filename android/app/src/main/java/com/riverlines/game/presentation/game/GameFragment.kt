package com.riverlines.game.presentation.game

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.riverlines.game.MainActivity
import com.riverlines.game.R
import com.riverlines.game.core.config.GameConfig
import com.riverlines.game.core.navigation.NavTransition
import com.riverlines.game.core.navigation.Navigator
import com.riverlines.game.core.ui.AppViewModelFactory
import com.riverlines.game.databinding.FragmentGameBinding
import com.riverlines.game.domain.model.GamePhase
import com.riverlines.game.presentation.gameover.GameOverFragment
import kotlinx.coroutines.launch

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameViewModel by viewModels { AppViewModelFactory() }

    private val handler = Handler(Looper.getMainLooper())
    private var lastWaveToken = 0
    private var navigated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBoard()
        setupActions()
        observeState()
        viewModel.start()
        measureBoard()
    }

    private fun setupBoard() {
        binding.gameBoard.setOnTileTapListener { index ->
            try {
                if (!isAdded || _binding == null) return@setOnTileTapListener
                val tileView = binding.gameBoard.tileViewAt(index)
                val consumed = viewModel.onTileTap(index)
                if (consumed) {
                    tileView?.animateTurn()
                } else {
                    tileView?.shake()
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun measureBoard() {
        binding.gameBoardArea.post {
            try {
                if (!isAdded || view == null) return@post
                val bound = _binding ?: return@post
                bound.gameBoard.configure(
                    bound.gameBoardArea.width,
                    bound.gameBoardArea.height
                )
            } catch (e: Exception) {
            }
        }
    }

    private fun setupActions() {
        binding.gameBackButton.setOnClickListener {
            val host = activity as? MainActivity ?: return@setOnClickListener
            host.navigator.popToRoot()
        }
        binding.gameCheckButton.setOnClickListener {
            viewModel.onCheckFlow()
        }
        binding.gameHintButton.setOnClickListener {
            viewModel.onHint()
        }
        binding.gameUndoButton.setOnClickListener {
            viewModel.onUndo()
        }
        binding.gameErrorRetry.setOnClickListener {
            viewModel.newLevel()
            measureBoard()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: GameUiState) {
        if (_binding == null) return

        if (state.error) {
            binding.gameErrorCard.visibility = View.VISIBLE
            binding.gameBoardCard.visibility = View.GONE
            return
        }

        binding.gameErrorCard.visibility = View.GONE
        binding.gameBoardCard.visibility = View.VISIBLE

        val board = state.board ?: return
        binding.gameBoard.bind(board, state.flooded, state.hintIndex)
        binding.gameLevelName.text = state.levelName
        binding.gameTurnsLeft.text = getString(R.string.game_turns_left, state.turnsLeft)
        binding.gameScore.text = getString(R.string.game_score, state.score)

        ViewCompat.setStateDescription(
            binding.gameTurnsLeft,
            getString(R.string.state_turns_left, state.turnsLeft)
        )
        ViewCompat.setStateDescription(
            binding.gameHintButton,
            getString(R.string.state_hints_left, state.hintsLeft)
        )
        ViewCompat.setStateDescription(
            binding.gameUndoButton,
            getString(
                if (state.undoLeft > 0) R.string.state_undo_available else R.string.state_undo_used
            )
        )

        val hintEnabled = state.hintsLeft > 0 && state.inputEnabled
        binding.gameHintButton.isEnabled = hintEnabled
        binding.gameHintButton.alpha = if (hintEnabled) 1f else 0.5f

        val undoEnabled = state.undoLeft > 0 && state.inputEnabled
        binding.gameUndoButton.isEnabled = undoEnabled
        binding.gameUndoButton.alpha = if (undoEnabled) 1f else 0.5f

        binding.gameCheckButton.isEnabled = state.inputEnabled
        binding.gameCheckButton.alpha = if (state.inputEnabled) 1f else 0.5f

        if (state.waveToken != lastWaveToken) {
            lastWaveToken = state.waveToken
            binding.gameBoard.playFlowWave(state.floodedOrder)
        }

        when (state.phase) {
            GamePhase.WIN -> scheduleResult(GameConfig.WIN_DELAY_MS)
            GamePhase.LOSE -> scheduleResult(GameConfig.LOSE_DELAY_MS)
            else -> Unit
        }
    }

    private fun scheduleResult(delay: Long) {
        if (navigated) return
        navigated = true
        handler.postDelayed({
            try {
                if (!isAdded || view == null || _binding == null) return@postDelayed
                val host = activity as? MainActivity ?: return@postDelayed
                host.navigator.swap(
                    Navigator.TAG_GAME,
                    GameOverFragment.newInstance(viewModel.result()),
                    Navigator.TAG_RESULT,
                    NavTransition.FADE
                )
            } catch (e: Exception) {
            }
        }, delay)
    }

    override fun onDestroyView() {
        handler.removeCallbacksAndMessages(null)
        _binding?.gameBoard?.cancelAnimations()
        _binding = null
        super.onDestroyView()
    }
}
