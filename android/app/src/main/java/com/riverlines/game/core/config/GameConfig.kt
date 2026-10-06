package com.riverlines.game.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L
    const val IDLE_TIMEOUT_MS = 20000L
    const val COLS = 6
    const val ROWS = 8
    const val TURN_SLACK = 6
    const val MIN_TURNS = 12
    const val MAX_TURNS = 24
    const val HINTS_PER_LEVEL = 3
    const val UNDO_PER_LEVEL = 1
    const val PATH_MIN = 12
    const val PATH_MAX = 22
    const val WETLANDS_MIN = 2
    const val WETLANDS_MAX = 3
    const val ROCKS_MIN = 3
    const val ROCKS_MAX = 5
    const val START_DROPS = 1240
    const val WIN_BASE_REWARD = 100
    const val REWARD_PER_TURN_LEFT = 10
    const val REWARD_PER_WETLAND = 30
    const val MIN_SCRAMBLE_TURNS = 6
    const val ROTATE_ANIM_MS = 180L
    const val FLOW_STAGGER_MS = 45L
    const val WIN_DELAY_MS = 900L
    const val LOSE_DELAY_MS = 1200L
    const val BOARD_MAX_WIDTH_DP = 380
    const val SCORE_PER_FLOODED = 10
    const val SCORE_PER_WETLAND = 20
}
