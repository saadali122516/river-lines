package com.riverlines.game.presentation.game

import com.riverlines.game.core.config.GameConfig
import com.riverlines.game.domain.model.BoardState
import com.riverlines.game.domain.model.GamePhase

data class GameUiState(
    val board: BoardState?,
    val flooded: Set<Int>,
    val floodedOrder: List<Int>,
    val waveToken: Int,
    val turnsLeft: Int,
    val turnsBudget: Int,
    val score: Int,
    val hintsLeft: Int,
    val undoLeft: Int,
    val hintIndex: Int,
    val phase: GamePhase,
    val flowPercent: Int,
    val wetlandsPassed: Int,
    val levelName: String,
    val error: Boolean
) {
    val inputEnabled: Boolean
        get() = phase == GamePhase.READY || phase == GamePhase.PLAYING

    companion object {
        fun empty(): GameUiState = GameUiState(
            board = null,
            flooded = emptySet(),
            floodedOrder = emptyList(),
            waveToken = 0,
            turnsLeft = 0,
            turnsBudget = 0,
            score = 0,
            hintsLeft = GameConfig.HINTS_PER_LEVEL,
            undoLeft = GameConfig.UNDO_PER_LEVEL,
            hintIndex = -1,
            phase = GamePhase.READY,
            flowPercent = 0,
            wetlandsPassed = 0,
            levelName = "",
            error = false
        )
    }
}
