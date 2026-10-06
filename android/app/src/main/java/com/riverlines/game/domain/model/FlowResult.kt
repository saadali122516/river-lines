package com.riverlines.game.domain.model

data class FlowResult(
    val floodedOrder: List<Int>,
    val reachedLake: Boolean,
    val flowPercent: Int,
    val wetlandsPassed: Int
) {
    val flooded: Set<Int>
        get() = floodedOrder.toSet()
}
