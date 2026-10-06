package com.riverlines.game.domain.model

data class BoardState(
    val cols: Int,
    val rows: Int,
    val tiles: List<PipeTile>,
    val path: List<Int>,
    val springIndex: Int,
    val lakeIndex: Int,
    val minTurns: Int,
    val levelName: String
) {
    fun withTile(tile: PipeTile): BoardState {
        val updated = tiles.toMutableList()
        updated[tile.index] = tile
        return copy(tiles = updated)
    }

    fun tileAt(row: Int, col: Int): PipeTile? {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return null
        return tiles[row * cols + col]
    }
}
