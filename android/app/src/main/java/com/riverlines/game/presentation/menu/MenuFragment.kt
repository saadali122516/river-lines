package com.riverlines.game.presentation.menu

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
import com.riverlines.game.databinding.FragmentMenuBinding
import com.riverlines.game.presentation.dialog.EcoChallengesDialog
import com.riverlines.game.presentation.dialog.TutorialDialog
import com.riverlines.game.presentation.game.GameFragment
import kotlinx.coroutines.launch

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MenuViewModel by viewModels { AppViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActions()
        observeState()
        playEntrance()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun setupActions() {
        binding.menuPlayButton.setOnClickListener {
            val host = activity as? MainActivity ?: return@setOnClickListener
            host.navigator.push(GameFragment(), Navigator.TAG_GAME, NavTransition.FORWARD)
        }
        binding.menuTutorialButton.setOnClickListener {
            TutorialDialog().show(parentFragmentManager, TutorialDialog.TAG)
        }
        binding.menuEcoButton.setOnClickListener {
            EcoChallengesDialog().show(parentFragmentManager, EcoChallengesDialog.TAG)
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

    private fun render(state: MenuUiState) {
        val context = context ?: return
        binding.menuBalanceValue.text = getString(R.string.menu_drops, groupDigits(state.drops))

        val accentFlow = ContextCompat.getColor(context, R.color.color_accent)
        val accentRivers = ContextCompat.getColor(context, R.color.color_primary)

        if (state.bestFlow > 0) {
            binding.menuStatFlow.visibility = View.VISIBLE
            binding.menuStatFlow.bind(
                R.drawable.ic_wave,
                getString(R.string.result_flow_value, state.bestFlow),
                getString(R.string.label_best_flow),
                accentFlow
            )
        } else {
            binding.menuStatFlow.visibility = View.GONE
        }

        if (state.riversDone > 0) {
            binding.menuStatRivers.visibility = View.VISIBLE
            binding.menuStatRivers.bind(
                R.drawable.ic_leaf_badge,
                state.riversDone.toString(),
                getString(R.string.label_rivers_done),
                accentRivers
            )
        } else {
            binding.menuStatRivers.visibility = View.GONE
        }

        binding.menuEcoHeader.bind(
            getString(R.string.menu_eco_title),
            getString(
                R.string.menu_eco_subtitle,
                state.challengesCompleted,
                state.challengesTotal
            )
        )
    }

    private fun playEntrance() {
        binding.menuTitle.alpha = 0f
        binding.menuTitle.translationY = -40f
        binding.menuTitle.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(320L)
            .start()

        binding.menuPlayTile.scaleX = 0.96f
        binding.menuPlayTile.scaleY = 0.96f
        binding.menuPlayTile.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(380L)
            .setInterpolator(OvershootInterpolator(0.8f))
            .start()

        binding.menuStatFlow.alpha = 0f
        binding.menuStatFlow.animate().alpha(1f).setStartDelay(90L).setDuration(300L).start()
        binding.menuStatRivers.alpha = 0f
        binding.menuStatRivers.animate().alpha(1f).setStartDelay(180L).setDuration(300L).start()

        binding.menuEcoTile.alpha = 0f
        binding.menuEcoTile.animate().alpha(1f).setStartDelay(260L).setDuration(300L).start()

        binding.menuSpring.animate()
            .scaleX(1.04f)
            .scaleY(1.04f)
            .setDuration(1200L)
            .withEndAction {
                try {
                    if (!isAdded || view == null || _binding == null) return@withEndAction
                    binding.menuSpring.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(1200L)
                        .start()
                } catch (e: Exception) {
                }
            }
            .start()
    }

    private fun groupDigits(value: Int): String {
        val raw = value.toString()
        if (raw.length <= 3) return raw
        val builder = StringBuilder()
        var counter = 0
        for (position in raw.length - 1 downTo 0) {
            builder.append(raw[position])
            counter += 1
            if (counter % 3 == 0 && position > 0) {
                builder.append(' ')
            }
        }
        return builder.reverse().toString()
    }

    override fun onDestroyView() {
        _binding?.let { bound ->
            bound.menuTitle.animate().cancel()
            bound.menuPlayTile.animate().cancel()
            bound.menuStatFlow.animate().cancel()
            bound.menuStatRivers.animate().cancel()
            bound.menuEcoTile.animate().cancel()
            bound.menuSpring.animate().cancel()
        }
        _binding = null
        super.onDestroyView()
    }
}
