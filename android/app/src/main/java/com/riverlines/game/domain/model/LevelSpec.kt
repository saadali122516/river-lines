package com.riverlines.game.domain.model

data class LevelSpec(
    val board: BoardState,
    val turnsBudget: Int
)
