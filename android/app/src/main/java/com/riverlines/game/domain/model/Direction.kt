package com.riverlines.game.domain.model

enum class Direction(val index: Int, val rowDelta: Int, val colDelta: Int) {
    UP(0, -1, 0),
    RIGHT(1, 0, 1),
    DOWN(2, 1, 0),
    LEFT(3, 0, -1);

    fun rotated(steps: Int): Direction {
        val next = ((index + steps) % 4 + 4) % 4
        return fromIndex(next)
    }

    fun opposite(): Direction = rotated(2)

    companion object {
        fun fromIndex(index: Int): Direction {
            val safe = ((index % 4) + 4) % 4
            return values().first { it.index == safe }
        }

        fun between(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Direction {
            return when {
                toRow < fromRow -> UP
                toRow > fromRow -> DOWN
                toCol > fromCol -> RIGHT
                else -> LEFT
            }
        }
    }
}
