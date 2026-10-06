package com.riverlines.game.domain.model

data class EcoChallenge(
    val id: String,
    val title: String,
    val description: String,
    val reward: Int,
    val completed: Boolean
)
