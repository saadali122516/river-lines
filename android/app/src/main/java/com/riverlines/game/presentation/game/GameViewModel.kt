package com.riverlines.game.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riverlines.game.core.config.GameConfig
import com.riverlines.game.domain.model.BoardState
import com.riverlines.game.domain.model.GamePhase
import com.riverlines.game.domain.model.RoundResult
import com.riverlines.game.domain.model.TileKind
import com.riverlines.game.domain.repository.LevelRepository
import com.riverlines.game.domain.usecase.EvaluateFlowUseCase
import com.riverlines.game.domain.usecase.RotateTileUseCase
import com.riverlines.game.domain.usecase.ScoreRoundUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val levelRepository: LevelRepository,
    private val rotateTile: RotateTileUseCase,
    private val evaluateFlow: EvaluateFlowUseCase,
    private val scoreRound: ScoreRoundUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState.empty())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private var board: BoardState? = null
    private var ecoNote = ""
    private var lastRotated = -1
    private var idleJob: Job? = null
    private var waveToken = 0

    fun start() {
        if (board != null) return
        newLevel()
    }

    fun newLevel() {
        idleJob?.cancel()
        try {
            val spec = levelRepository.nextLevel()
            ecoNote = levelRepository.ecoNote()
            board = spec.board
            lastRotated = -1
            waveToken += 1
            val flow = evaluateFlow(spec.board)
            _state.value = GameUiState(
                board = spec.board,
                flooded = flow.flooded,
                floodedOrder = flow.floodedOrder,
                waveToken = waveToken,
                turnsLeft = spec.turnsBudget,
                turnsBudget = spec.turnsBudget,
                score = scoreOf(spec.board, flow.floodedOrder, flow.wetlandsPassed),
                hintsLeft = GameConfig.HINTS_PER_LEVEL,
                undoLeft = GameConfig.UNDO_PER_LEVEL,
                hintIndex = -1,
                phase = GamePhase.READY,
                flowPercent = flow.flowPercent,
                wetlandsPassed = flow.wetlandsPassed,
                levelName = spec.board.levelName,
                error = false
            )
            restartIdleTimer()
        } catch (e: Exception) {
            board = null
            _state.value = GameUiState.empty().copy(error = true)
        }
    }

    fun onTileTap(index: Int): Boolean {
        val current = _state.value
        val active = board ?: return false
        if (!current.inputEnabled) return false
        val tile = active.tiles.getOrNull(index) ?: return false
        if (!tile.kind.rotatable) return false
        val updated = rotateTile(active, index, true) ?: return false
        board = updated
        lastRotated = index
        consumeTurn(updated, current.turnsLeft - 1, current.hintsLeft, current.undoLeft)
        restartIdleTimer()
        return true
    }

    fun onCheckFlow() {
        val current = _state.value
        val active = board ?: return
        if (!current.inputEnabled) return
        consumeTurn(active, current.turnsLeft - 1, current.hintsLeft, current.undoLeft)
        restartIdleTimer()
    }

    fun onHint() {
        val current = _state.value
        val active = board ?: return
        if (!current.inputEnabled || current.hintsLeft <= 0) return
        val wrong = active.path.firstOrNull { index ->
            val tile = active.tiles[index]
            tile.kind.rotatable && !tile.isSolved
        } ?: return
        _state.value = current.copy(hintIndex = wrong, hintsLeft = current.hintsLeft - 1)
        restartIdleTimer()
    }

    fun onUndo() {
        val current = _state.value
        val active = board ?: return
        if (!current.inputEnabled || current.undoLeft <= 0) return
        val target = lastRotated
        if (target < 0) return
        val updated = rotateTile(active, target, false) ?: return
        board = updated
        lastRotated = -1
        val flow = evaluateFlow(updated)
        waveToken += 1
        _state.value = current.copy(
            board = updated,
            flooded = flow.flooded,
            floodedOrder = flow.floodedOrder,
            waveToken = waveToken,
            turnsLeft = (current.turnsLeft + 1).coerceAtMost(current.turnsBudget),
            undoLeft = current.undoLeft - 1,
            hintIndex = -1,
            score = scoreOf(updated, flow.floodedOrder, flow.wetlandsPassed),
            flowPercent = flow.flowPercent,
            wetlandsPassed = flow.wetlandsPassed,
            phase = GamePhase.PLAYING
        )
        restartIdleTimer()
    }

    fun finishByIdle() {
        val current = _state.value
        if (!current.inputEnabled) return
        _state.value = current.copy(phase = GamePhase.LOSE)
        idleJob?.cancel()
    }

    fun result(): RoundResult {
        val current = _state.value
        val win = current.phase == GamePhase.WIN
        val turnsUsed = (current.turnsBudget - current.turnsLeft).coerceAtLeast(0)
        return RoundResult(
            win = win,
            levelName = current.levelName,
            flowPercent = current.flowPercent,
            turnsUsed = turnsUsed,
            turnsBudget = current.turnsBudget,
            wetlandsPassed = current.wetlandsPassed,
            dropsEarned = scoreRound(win, current.turnsLeft, current.wetlandsPassed),
            ecoNote = ecoNote
        )
    }

    override fun onCleared() {
        idleJob?.cancel()
        idleJob = null
        super.onCleared()
    }

    private fun consumeTurn(updated: BoardState, turnsLeft: Int, hintsLeft: Int, undoLeft: Int) {
        val flow = evaluateFlow(updated)
        waveToken += 1
        val remaining = turnsLeft.coerceAtLeast(0)
        val phase = when {
            flow.reachedLake -> GamePhase.WIN
            remaining <= 0 -> GamePhase.LOSE
            else -> GamePhase.PLAYING
        }
        _state.value = _state.value.copy(
            board = updated,
            flooded = flow.flooded,
            floodedOrder = flow.floodedOrder,
            waveToken = waveToken,
            turnsLeft = remaining,
            hintsLeft = hintsLeft,
            undoLeft = undoLeft,
            hintIndex = -1,
            score = scoreOf(updated, flow.floodedOrder, flow.wetlandsPassed),
            flowPercent = flow.flowPercent,
            wetlandsPassed = flow.wetlandsPassed,
            phase = phase,
            error = false
        )
        if (phase != GamePhase.PLAYING) {
            idleJob?.cancel()
        }
    }

    private fun scoreOf(board: BoardState, order: List<Int>, wetlands: Int): Int {
        val carriers = order.count { board.tiles[it].kind != TileKind.EMPTY }
        return carriers * GameConfig.SCORE_PER_FLOODED + wetlands * GameConfig.SCORE_PER_WETLAND
    }

    private fun restartIdleTimer() {
        idleJob?.cancel()
        idleJob = viewModelScope.launch {
            delay(GameConfig.IDLE_TIMEOUT_MS)
            finishByIdle()
        }
    }
}
