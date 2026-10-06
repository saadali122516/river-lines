package com.riverlines.game.data.local

import android.content.Context
import android.content.SharedPreferences
import com.riverlines.game.core.config.GameConfig

class RiverPrefs(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    var drops: Int
        get() = prefs.getInt(KEY_DROPS, GameConfig.START_DROPS)
        set(value) = prefs.edit().putInt(KEY_DROPS, value).apply()

    var bestFlow: Int
        get() = prefs.getInt(KEY_BEST_FLOW, DEFAULT_BEST_FLOW)
        set(value) = prefs.edit().putInt(KEY_BEST_FLOW, value).apply()

    var riversDone: Int
        get() = prefs.getInt(KEY_RIVERS_DONE, DEFAULT_RIVERS_DONE)
        set(value) = prefs.edit().putInt(KEY_RIVERS_DONE, value).apply()

    var levelCursor: Int
        get() = prefs.getInt(KEY_LEVEL_CURSOR, 0)
        set(value) = prefs.edit().putInt(KEY_LEVEL_CURSOR, value).apply()

    companion object {
        private const val STORE_NAME = "river_lines_store"
        private const val KEY_DROPS = "drops"
        private const val KEY_BEST_FLOW = "best_flow"
        private const val KEY_RIVERS_DONE = "rivers_done"
        private const val KEY_LEVEL_CURSOR = "level_cursor"
        private const val DEFAULT_BEST_FLOW = 98
        private const val DEFAULT_RIVERS_DONE = 6
    }
}
