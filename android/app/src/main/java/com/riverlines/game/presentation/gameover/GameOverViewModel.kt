package com.riverlines.game.presentation.gameover

import androidx.lifecycle.ViewModel
import com.riverlines.game.domain.model.RoundResult
import com.riverlines.game.domain.usecase.SaveProgressUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameOverViewModel(
    private val saveProgress: SaveProgressUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState.empty())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    private var submitted = false

    fun submit(result: RoundResult) {
        if (submitted) return
        submitted = true
        val progress = saveProgress(result)
        _state.value = GameOverUiState(
            win = result.win,
            levelName = result.levelName,
            flowPercent = result.flowPercent,
            turnsUsed = result.turnsUsed,
            turnsBudget = result.turnsBudget,
            dropsEarned = result.dropsEarned,
            totalDrops = progress.drops,
            ecoNote = result.ecoNote
        )
    }
}
