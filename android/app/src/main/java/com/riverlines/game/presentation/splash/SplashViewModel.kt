package com.riverlines.game.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riverlines.game.core.config.GameConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    private val _progressLabel = MutableStateFlow(0)
    val progressLabel: StateFlow<Int> = _progressLabel.asStateFlow()

    private var timerJob: Job? = null

    fun start() {
        if (timerJob != null) return
        timerJob = viewModelScope.launch {
            val steps = STEP_COUNT
            val slice = GameConfig.LOADER_DURATION_MS / steps
            for (step in 1..steps) {
                delay(slice)
                _progressLabel.value = step * 100 / steps
            }
            _ready.value = true
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        timerJob = null
        super.onCleared()
    }

    private companion object {
        const val STEP_COUNT = 8
    }
}
