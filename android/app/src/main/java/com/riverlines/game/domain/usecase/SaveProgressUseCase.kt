package com.riverlines.game.domain.usecase

import com.riverlines.game.domain.model.RiverProgress
import com.riverlines.game.domain.model.RoundResult
import com.riverlines.game.domain.repository.ProgressRepository

class SaveProgressUseCase(private val repository: ProgressRepository) {

    operator fun invoke(result: RoundResult): RiverProgress {
        val current = repository.load()
        val updated = RiverProgress(
            drops = current.drops + result.dropsEarned,
            bestFlow = maxOf(current.bestFlow, result.flowPercent),
            riversDone = if (result.win) current.riversDone + 1 else current.riversDone
        )
        repository.save(updated)
        return updated
    }
}
