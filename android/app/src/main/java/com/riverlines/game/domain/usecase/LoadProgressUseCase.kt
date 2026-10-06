package com.riverlines.game.domain.usecase

import com.riverlines.game.domain.model.RiverProgress
import com.riverlines.game.domain.repository.ProgressRepository

class LoadProgressUseCase(private val repository: ProgressRepository) {

    operator fun invoke(): RiverProgress = repository.load()
}
