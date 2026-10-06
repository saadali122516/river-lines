package com.riverlines.game.domain.repository

import com.riverlines.game.domain.model.EcoChallenge
import com.riverlines.game.domain.model.RiverProgress

interface ProgressRepository {
    fun load(): RiverProgress
    fun save(progress: RiverProgress)
    fun challenges(): List<EcoChallenge>
}
