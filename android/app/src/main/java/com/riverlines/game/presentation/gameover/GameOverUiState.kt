package com.riverlines.game.presentation.gameover

data class GameOverUiState(
    val win: Boolean,
    val levelName: String,
    val flowPercent: Int,
    val turnsUsed: Int,
    val turnsBudget: Int,
    val dropsEarned: Int,
    val totalDrops: Int,
    val ecoNote: String
) {
    companion object {
        fun empty(): GameOverUiState = GameOverUiState(
            win = false,
            levelName = "",
            flowPercent = 0,
            turnsUsed = 0,
            turnsBudget = 0,
            dropsEarned = 0,
            totalDrops = 0,
            ecoNote = ""
        )
    }
}
