package com.riverlines.game.domain.repository

import com.riverlines.game.domain.model.LevelSpec

interface LevelRepository {
    fun nextLevel(): LevelSpec
    fun ecoNote(): String
}
