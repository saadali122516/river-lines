package com.riverlines.game.presentation.menu

import androidx.lifecycle.ViewModel
import com.riverlines.game.domain.repository.ProgressRepository
import com.riverlines.game.domain.usecase.LoadProgressUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuViewModel(
    private val loadProgress: LoadProgressUseCase,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState.empty())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val progress = loadProgress()
        val challenges = progressRepository.challenges()
        _state.value = MenuUiState(
            drops = progress.drops,
            bestFlow = progress.bestFlow,
            riversDone = progress.riversDone,
            challengesCompleted = challenges.count { it.completed },
            challengesTotal = challenges.size
        )
    }
}
