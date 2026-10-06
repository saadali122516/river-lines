package com.riverlines.game.domain.model

data class PipeTile(
    val index: Int,
    val row: Int,
    val col: Int,
    val kind: TileKind,
    val rotation: Int,
    val targetRotation: Int,
    val onPath: Boolean
) {
    val openings: Set<Direction>
        get() = kind.openingsAt(rotation)

    val isSolved: Boolean
        get() = kind.openingsAt(rotation) == kind.openingsAt(targetRotation)

    fun turned(): PipeTile = copy(rotation = (rotation + 1) % 4)

    fun turnedBack(): PipeTile = copy(rotation = ((rotation - 1) % 4 + 4) % 4)
}
