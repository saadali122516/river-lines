package com.riverlines.game.domain.usecase

import com.riverlines.game.domain.model.BoardState
import com.riverlines.game.domain.model.FlowResult
import com.riverlines.game.domain.model.TileKind

class EvaluateFlowUseCase {

    operator fun invoke(board: BoardState): FlowResult {
        val order = ArrayList<Int>()
        val seen = HashSet<Int>()
        val queue = ArrayDeque<Int>()

        order.add(board.springIndex)
        seen.add(board.springIndex)
        queue.addLast(board.springIndex)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val tile = board.tiles[current]
            for (direction in tile.openings) {
                val neighbourRow = tile.row + direction.rowDelta
                val neighbourCol = tile.col + direction.colDelta
                val neighbour = board.tileAt(neighbourRow, neighbourCol) ?: continue
                if (seen.contains(neighbour.index)) continue
                if (!neighbour.kind.isCarrier) continue
                if (!neighbour.openings.contains(direction.opposite())) continue
                seen.add(neighbour.index)
                order.add(neighbour.index)
                queue.addLast(neighbour.index)
            }
        }

        val pathFlooded = board.path.count { seen.contains(it) }
        val percent = if (board.path.isEmpty()) 0 else pathFlooded * 100 / board.path.size
        val wetlands = order.count { board.tiles[it].kind == TileKind.WETLAND }

        return FlowResult(
            floodedOrder = order,
            reachedLake = seen.contains(board.lakeIndex),
            flowPercent = percent,
            wetlandsPassed = wetlands
        )
    }
}
