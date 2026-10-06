package com.riverlines.game.data.repository

import com.riverlines.game.data.local.RiverPrefs
import com.riverlines.game.data.sample.SampleData
import com.riverlines.game.domain.model.LevelSpec
import com.riverlines.game.domain.repository.LevelRepository
import com.riverlines.game.domain.usecase.GenerateLevelUseCase

class LevelRepositoryImpl(
    private val prefs: RiverPrefs,
    private val generateLevel: GenerateLevelUseCase
) : LevelRepository {

    override fun nextLevel(): LevelSpec {
        val cursor = prefs.levelCursor
        val name = SampleData.riverNames[cursor % SampleData.riverNames.size]
        prefs.levelCursor = cursor + 1
        return generateLevel(name)
    }

    override fun ecoNote(): String {
        val notes = SampleData.ecoNotes
        val cursor = prefs.levelCursor
        return notes[((cursor % notes.size) + notes.size) % notes.size]
    }
}
