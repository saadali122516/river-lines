package com.riverlines.game.domain.usecase

import com.riverlines.game.core.config.GameConfig

class ScoreRoundUseCase {

    operator fun invoke(win: Boolean, turnsLeft: Int, wetlandsPassed: Int): Int {
        if (!win) return 0
        return GameConfig.WIN_BASE_REWARD +
            GameConfig.REWARD_PER_TURN_LEFT * turnsLeft.coerceAtLeast(0) +
            GameConfig.REWARD_PER_WETLAND * wetlandsPassed.coerceAtLeast(0)
    }
}
