package com.riverlines.game.presentation.gameover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.riverlines.game.MainActivity
import com.riverlines.game.R
import com.riverlines.game.core.navigation.NavTransition
import com.riverlines.game.core.navigation.Navigator
import com.riverlines.game.core.ui.AppViewModelFactory
import com.riverlines.game.databinding.FragmentGameOverBinding
import com.riverlines.game.domain.model.RoundResult
import com.riverlines.game.presentation.game.GameFragment
import kotlinx.coroutines.launch

class GameOverFragment : Fragment() {

    private var _binding: FragmentGameOverBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameOverViewModel by viewModels { AppViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameOverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActions()
        observeState()
        viewModel.submit(readResult())
        playEntrance()
    }

    private fun setupActions() {
        binding.resultPlayAgain.setOnClickListener {
            val host = activity as? MainActivity ?: return@setOnClickListener
            host.navigator.swap(
                Navigator.TAG_RESULT,
                GameFragment(),
                Navigator.TAG_GAME,
                NavTransition.FORWARD
            )
        }
        binding.resultMenu.setOnClickListener {
            val host = activity as? MainActivity ?: return@setOnClickListener
            host.navigator.popToRoot()
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

    private fun render(state: GameOverUiState) {
        val context = context ?: return
        if (_binding == null) return

        val titleColor = if (state.win) {
            ContextCompat.getColor(context, R.color.color_primary)
        } else {
            ContextCompat.getColor(context, R.color.color_danger)
        }
        binding.resultTitle.setText(if (state.win) R.string.result_win else R.string.result_lose)
        binding.resultTitle.setTextColor(titleColor)

        binding.resultHero.setImageResource(
            if (state.win) R.drawable.sprite_lake else R.drawable.sprite_rock
        )
        binding.resultHero.contentDescription = getString(
            if (state.win) R.string.cd_lake else R.string.cd_rock
        )

        binding.resultNoteText.text = state.ecoNote

        var visible = 0

        if (state.flowPercent > 0) {
            visible += 1
            binding.resultStatFlow.visibility = View.VISIBLE
            binding.resultStatFlow.bind(
                R.drawable.ic_wave,
                getString(R.string.result_flow_value, state.flowPercent),
                getString(R.string.label_flow),
                ContextCompat.getColor(context, R.color.color_accent)
            )
        } else {
            binding.resultStatFlow.visibility = View.GONE
        }

        if (state.turnsUsed > 0) {
            visible += 1
            binding.resultStatTurns.visibility = View.VISIBLE
            binding.resultStatTurns.bind(
                R.drawable.ic_undo,
                getString(R.string.result_turns_value, state.turnsUsed, state.turnsBudget),
                getString(R.string.label_turns),
                ContextCompat.getColor(context, R.color.color_primary)
            )
        } else {
            binding.resultStatTurns.visibility = View.GONE
        }

        if (state.dropsEarned > 0) {
            visible += 1
            binding.resultStatDrops.visibility = View.VISIBLE
            binding.resultStatDrops.bind(
                R.drawable.ic_leaf_badge,
                getString(R.string.result_drops_value, state.dropsEarned),
                getString(R.string.label_drops),
                ContextCompat.getColor(context, R.color.color_secondary)
            )
        } else {
            binding.resultStatDrops.visibility = View.GONE
        }

        binding.resultStatsRow.visibility = if (visible >= 2) View.VISIBLE else View.GONE
    }

    private fun playEntrance() {
        binding.resultTitle.alpha = 0f
        binding.resultTitle.translationY = -32f
        binding.resultTitle.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(360L)
            .start()

        binding.resultHero.scaleX = 0.9f
        binding.resultHero.scaleY = 0.9f
        binding.resultHero.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(420L)
            .setInterpolator(OvershootInterpolator(1.1f))
            .start()

        binding.resultStatFlow.alpha = 0f
        binding.resultStatFlow.animate().alpha(1f).setStartDelay(80L).setDuration(280L).start()
        binding.resultStatTurns.alpha = 0f
        binding.resultStatTurns.animate().alpha(1f).setStartDelay(160L).setDuration(280L).start()
        binding.resultStatDrops.alpha = 0f
        binding.resultStatDrops.animate().alpha(1f).setStartDelay(240L).setDuration(280L).start()
    }

    private fun readResult(): RoundResult {
        val args = arguments
        return RoundResult(
            win = args?.getBoolean(ARG_WIN) ?: false,
            levelName = args?.getString(ARG_LEVEL).orEmpty(),
            flowPercent = args?.getInt(ARG_FLOW) ?: 0,
            turnsUsed = args?.getInt(ARG_TURNS_USED) ?: 0,
            turnsBudget = args?.getInt(ARG_TURNS_BUDGET) ?: 0,
            wetlandsPassed = args?.getInt(ARG_WETLANDS) ?: 0,
            dropsEarned = args?.getInt(ARG_DROPS) ?: 0,
            ecoNote = args?.getString(ARG_NOTE).orEmpty()
        )
    }

    override fun onDestroyView() {
        _binding?.let { bound ->
            bound.resultTitle.animate().cancel()
            bound.resultHero.animate().cancel()
            bound.resultStatFlow.animate().cancel()
            bound.resultStatTurns.animate().cancel()
            bound.resultStatDrops.animate().cancel()
        }
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_WIN = "arg_win"
        private const val ARG_LEVEL = "arg_level"
        private const val ARG_FLOW = "arg_flow"
        private const val ARG_TURNS_USED = "arg_turns_used"
        private const val ARG_TURNS_BUDGET = "arg_turns_budget"
        private const val ARG_WETLANDS = "arg_wetlands"
        private const val ARG_DROPS = "arg_drops"
        private const val ARG_NOTE = "arg_note"

        fun newInstance(result: RoundResult): GameOverFragment {
            val fragment = GameOverFragment()
            val args = Bundle()
            args.putBoolean(ARG_WIN, result.win)
            args.putString(ARG_LEVEL, result.levelName)
            args.putInt(ARG_FLOW, result.flowPercent)
            args.putInt(ARG_TURNS_USED, result.turnsUsed)
            args.putInt(ARG_TURNS_BUDGET, result.turnsBudget)
            args.putInt(ARG_WETLANDS, result.wetlandsPassed)
            args.putInt(ARG_DROPS, result.dropsEarned)
            args.putString(ARG_NOTE, result.ecoNote)
            fragment.arguments = args
            return fragment
        }
    }
}
