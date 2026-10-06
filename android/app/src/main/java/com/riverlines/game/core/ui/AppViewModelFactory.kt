package com.riverlines.game.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.riverlines.game.core.di.ServiceLocator
import com.riverlines.game.presentation.game.GameViewModel
import com.riverlines.game.presentation.gameover.GameOverViewModel
import com.riverlines.game.presentation.menu.MenuViewModel
import com.riverlines.game.presentation.splash.SplashViewModel

class AppViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel() as T

            modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(
                ServiceLocator.loadProgress(),
                ServiceLocator.progressRepository()
            ) as T

            modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(
                ServiceLocator.levelRepository(),
                ServiceLocator.rotateTile,
                ServiceLocator.evaluateFlow,
                ServiceLocator.scoreRound
            ) as T

            modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel(
                ServiceLocator.saveProgress()
            ) as T

            else -> throw IllegalArgumentException("Unknown ViewModel " + modelClass.name)
        }
    }
}
