package com.riverlines.game.domain.model

enum class TileKind(val baseOpenings: Set<Direction>, val rotatable: Boolean) {
    SPRING(setOf(Direction.DOWN), false),
    LAKE(setOf(Direction.UP), false),
    STRAIGHT(setOf(Direction.UP, Direction.DOWN), true),
    ELBOW(setOf(Direction.UP, Direction.RIGHT), true),
    TEE(setOf(Direction.UP, Direction.RIGHT, Direction.DOWN), true),
    CROSS(setOf(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT), true),
    WETLAND(setOf(Direction.UP, Direction.RIGHT), true),
    ROCK(emptySet(), false),
    EMPTY(emptySet(), false);

    fun openingsAt(rotation: Int): Set<Direction> =
        baseOpenings.map { it.rotated(rotation) }.toSet()

    fun rotationFor(openings: Set<Direction>): Int {
        for (step in 0 until 4) {
            if (openingsAt(step) == openings) return step
        }
        return 0
    }

    fun stepsToMatch(current: Int, target: Int): Int {
        val wanted = openingsAt(target)
        for (step in 0 until 4) {
            if (openingsAt(current + step) == wanted) return step
        }
        return 0
    }

    val isCarrier: Boolean
        get() = this != EMPTY && this != ROCK
}
