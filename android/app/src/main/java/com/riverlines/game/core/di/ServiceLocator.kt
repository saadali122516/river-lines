package com.riverlines.game.core.di

import android.content.Context
import com.riverlines.game.data.local.RiverPrefs
import com.riverlines.game.data.repository.LevelRepositoryImpl
import com.riverlines.game.data.repository.ProgressRepositoryImpl
import com.riverlines.game.domain.repository.LevelRepository
import com.riverlines.game.domain.repository.ProgressRepository
import com.riverlines.game.domain.usecase.EvaluateFlowUseCase
import com.riverlines.game.domain.usecase.GenerateLevelUseCase
import com.riverlines.game.domain.usecase.LoadProgressUseCase
import com.riverlines.game.domain.usecase.RotateTileUseCase
import com.riverlines.game.domain.usecase.SaveProgressUseCase
import com.riverlines.game.domain.usecase.ScoreRoundUseCase

object ServiceLocator {

    private var prefs: RiverPrefs? = null
    private var progressRepo: ProgressRepository? = null
    private var levelRepo: LevelRepository? = null

    val generateLevel = GenerateLevelUseCase()
    val rotateTile = RotateTileUseCase()
    val evaluateFlow = EvaluateFlowUseCase()
    val scoreRound = ScoreRoundUseCase()

    fun init(context: Context) {
        if (prefs != null) return
        val store = RiverPrefs(context.applicationContext)
        prefs = store
        progressRepo = ProgressRepositoryImpl(store)
        levelRepo = LevelRepositoryImpl(store, generateLevel)
    }

    fun progressRepository(): ProgressRepository = requireNotNull(progressRepo) {
        "ServiceLocator is not initialised"
    }

    fun levelRepository(): LevelRepository = requireNotNull(levelRepo) {
        "ServiceLocator is not initialised"
    }

    fun loadProgress(): LoadProgressUseCase = LoadProgressUseCase(progressRepository())

    fun saveProgress(): SaveProgressUseCase = SaveProgressUseCase(progressRepository())
}
