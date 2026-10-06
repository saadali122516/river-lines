package com.riverlines.game.domain.usecase

import com.riverlines.game.domain.model.BoardState

class RotateTileUseCase {

    operator fun invoke(board: BoardState, index: Int, forward: Boolean): BoardState? {
        if (index < 0 || index >= board.tiles.size) return null
        val tile = board.tiles[index]
        if (!tile.kind.rotatable) return null
        val updated = if (forward) tile.turned() else tile.turnedBack()
        return board.withTile(updated)
    }
}
