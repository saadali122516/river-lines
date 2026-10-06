package com.riverlines.game.presentation.menu

data class MenuUiState(
    val drops: Int,
    val bestFlow: Int,
    val riversDone: Int,
    val challengesCompleted: Int,
    val challengesTotal: Int
) {
    companion object {
        fun empty(): MenuUiState = MenuUiState(
            drops = 0,
            bestFlow = 0,
            riversDone = 0,
            challengesCompleted = 0,
            challengesTotal = 0
        )
    }
}
