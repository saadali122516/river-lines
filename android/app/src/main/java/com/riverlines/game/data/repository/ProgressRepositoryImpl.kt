package com.riverlines.game.data.repository

import com.riverlines.game.data.local.RiverPrefs
import com.riverlines.game.data.sample.SampleData
import com.riverlines.game.domain.model.EcoChallenge
import com.riverlines.game.domain.model.RiverProgress
import com.riverlines.game.domain.repository.ProgressRepository

class ProgressRepositoryImpl(private val prefs: RiverPrefs) : ProgressRepository {

    override fun load(): RiverProgress = RiverProgress(
        drops = prefs.drops,
        bestFlow = prefs.bestFlow,
        riversDone = prefs.riversDone
    )

    override fun save(progress: RiverProgress) {
        prefs.drops = progress.drops
        prefs.bestFlow = progress.bestFlow
        prefs.riversDone = progress.riversDone
    }

    override fun challenges(): List<EcoChallenge> = SampleData.ecoChallenges
}
