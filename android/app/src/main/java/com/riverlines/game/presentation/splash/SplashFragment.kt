package com.riverlines.game.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.riverlines.game.MainActivity
import com.riverlines.game.core.navigation.NavTransition
import com.riverlines.game.core.navigation.Navigator
import com.riverlines.game.core.ui.AppViewModelFactory
import com.riverlines.game.databinding.FragmentSplashBinding
import com.riverlines.game.presentation.menu.MenuFragment
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SplashViewModel by viewModels { AppViewModelFactory() }

    private var animator: SplashAnimator? = null
    private var navigated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animator = SplashAnimator(binding)
        animator?.start()
        viewModel.start()
        observeReady()
    }

    private fun observeReady() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ready.collect { ready ->
                    if (ready) {
                        goToMenu()
                    }
                }
            }
        }
    }

    private fun goToMenu() {
        try {
            if (navigated) return
            if (!isAdded || view == null || _binding == null) return
            val host = activity as? MainActivity ?: return
            navigated = true
            host.navigator.setRoot(
                MenuFragment(),
                Navigator.TAG_MENU,
                NavTransition.FADE
            )
        } catch (e: Exception) {
        }
    }

    override fun onDestroyView() {
        animator?.cancel()
        animator = null
        _binding = null
        super.onDestroyView()
    }
}
