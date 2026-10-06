package com.riverlines.game.domain.model

data class RoundResult(
    val win: Boolean,
    val levelName: String,
    val flowPercent: Int,
    val turnsUsed: Int,
    val turnsBudget: Int,
    val wetlandsPassed: Int,
    val dropsEarned: Int,
    val ecoNote: String
)
