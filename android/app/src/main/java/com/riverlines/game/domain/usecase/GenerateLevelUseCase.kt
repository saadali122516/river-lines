package com.riverlines.game.domain.usecase

import com.riverlines.game.core.config.GameConfig
import com.riverlines.game.domain.model.BoardState
import com.riverlines.game.domain.model.Direction
import com.riverlines.game.domain.model.LevelSpec
import com.riverlines.game.domain.model.PipeTile
import com.riverlines.game.domain.model.TileKind
import kotlin.random.Random

class GenerateLevelUseCase {

    operator fun invoke(levelName: String, random: Random = Random.Default): LevelSpec {
        val cols = GameConfig.COLS
        val rows = GameConfig.ROWS
        val path = randomPath(cols, rows, random) ?: fallbackPath(cols, rows, random)

        val kinds = Array(cols * rows) { TileKind.EMPTY }
        val targets = IntArray(cols * rows)
        val onPath = BooleanArray(cols * rows)

        for (step in path.indices) {
            val cell = path[step]
            onPath[cell] = true
            val row = cell / cols
            val col = cell % cols
            when (step) {
                0 -> {
                    val next = path[1]
                    val dir = Direction.between(row, col, next / cols, next % cols)
                    kinds[cell] = TileKind.SPRING
                    targets[cell] = TileKind.SPRING.rotationFor(setOf(dir))
                }

                path.lastIndex -> {
                    val prev = path[step - 1]
                    val dir = Direction.between(row, col, prev / cols, prev % cols)
                    kinds[cell] = TileKind.LAKE
                    targets[cell] = TileKind.LAKE.rotationFor(setOf(dir))
                }

                else -> {
                    val prev = path[step - 1]
                    val next = path[step + 1]
                    val toPrev = Direction.between(row, col, prev / cols, prev % cols)
                    val toNext = Direction.between(row, col, next / cols, next % cols)
                    val openings = setOf(toPrev, toNext)
                    val kind = if (toPrev.opposite() == toNext) TileKind.STRAIGHT else TileKind.ELBOW
                    kinds[cell] = kind
                    targets[cell] = kind.rotationFor(openings)
                }
            }
        }

        applyWetlands(path, kinds, random)
        applyScenery(cols, rows, onPath, kinds, targets, random)

        val rotations = IntArray(cols * rows) { targets[it] }
        var minTurns = scramble(path, kinds, targets, rotations, random)
        var guard = 0
        while (minTurns < GameConfig.MIN_SCRAMBLE_TURNS && guard < 30) {
            minTurns = scramble(path, kinds, targets, rotations, random)
            guard += 1
        }

        val tiles = ArrayList<PipeTile>(cols * rows)
        for (index in 0 until cols * rows) {
            tiles.add(
                PipeTile(
                    index = index,
                    row = index / cols,
                    col = index % cols,
                    kind = kinds[index],
                    rotation = rotations[index],
                    targetRotation = targets[index],
                    onPath = onPath[index]
                )
            )
        }

        val board = BoardState(
            cols = cols,
            rows = rows,
            tiles = tiles,
            path = path,
            springIndex = path.first(),
            lakeIndex = path.last(),
            minTurns = minTurns,
            levelName = levelName
        )

        val budget = (minTurns + GameConfig.TURN_SLACK)
            .coerceAtLeast(GameConfig.MIN_TURNS)
            .coerceAtMost(GameConfig.MAX_TURNS)

        return LevelSpec(board = board, turnsBudget = budget)
    }

    private fun scramble(
        path: List<Int>,
        kinds: Array<TileKind>,
        targets: IntArray,
        rotations: IntArray,
        random: Random
    ): Int {
        var total = 0
        for (step in path.indices) {
            val cell = path[step]
            val kind = kinds[cell]
            if (!kind.rotatable) {
                rotations[cell] = targets[cell]
                continue
            }
            val shift = random.nextInt(4)
            rotations[cell] = (targets[cell] + shift) % 4
            total += kind.stepsToMatch(rotations[cell], targets[cell])
        }
        return total
    }

    private fun applyWetlands(path: List<Int>, kinds: Array<TileKind>, random: Random) {
        val candidates = path.drop(1).dropLast(1).filter { kinds[it] == TileKind.ELBOW }
        if (candidates.isEmpty()) return
        val wanted = random.nextInt(GameConfig.WETLANDS_MIN, GameConfig.WETLANDS_MAX + 1)
        candidates.shuffled(random).take(wanted).forEach { kinds[it] = TileKind.WETLAND }
    }

    private fun applyScenery(
        cols: Int,
        rows: Int,
        onPath: BooleanArray,
        kinds: Array<TileKind>,
        targets: IntArray,
        random: Random
    ) {
        val free = (0 until cols * rows).filter { !onPath[it] }
        if (free.isEmpty()) return
        val shuffled = free.shuffled(random)
        val rockCount = random.nextInt(GameConfig.ROCKS_MIN, GameConfig.ROCKS_MAX + 1)
            .coerceAtMost(shuffled.size)
        for (index in 0 until rockCount) {
            kinds[shuffled[index]] = TileKind.ROCK
        }
        val decorPool = listOf(TileKind.STRAIGHT, TileKind.ELBOW, TileKind.TEE)
        for (index in rockCount until shuffled.size) {
            if (random.nextInt(100) >= 32) continue
            val cell = shuffled[index]
            val kind = decorPool[random.nextInt(decorPool.size)]
            kinds[cell] = kind
            targets[cell] = random.nextInt(4)
        }
    }

    private fun randomPath(cols: Int, rows: Int, random: Random): List<Int>? {
        repeat(240) {
            val path = ArrayList<Int>()
            val visited = HashSet<Int>()
            var row = 0
            var col = random.nextInt(cols)
            path.add(row * cols + col)
            visited.add(row * cols + col)
            var guard = 0
            while (guard < 400) {
                guard += 1
                if (row == rows - 1 && path.size >= GameConfig.PATH_MIN) return path
                val options = ArrayList<Direction>(3)
                if (row + 1 < rows && !visited.contains((row + 1) * cols + col)) {
                    options.add(Direction.DOWN)
                }
                if (col - 1 >= 0 && !visited.contains(row * cols + col - 1)) {
                    options.add(Direction.LEFT)
                }
                if (col + 1 < cols && !visited.contains(row * cols + col + 1)) {
                    options.add(Direction.RIGHT)
                }
                if (options.isEmpty()) break
                val forceDown = path.size >= GameConfig.PATH_MAX - 1
                val pick = when {
                    forceDown && options.contains(Direction.DOWN) -> Direction.DOWN
                    options.contains(Direction.DOWN) && random.nextInt(100) < 52 -> Direction.DOWN
                    else -> options[random.nextInt(options.size)]
                }
                row += pick.rowDelta
                col += pick.colDelta
                val cell = row * cols + col
                path.add(cell)
                visited.add(cell)
                if (path.size > GameConfig.PATH_MAX) break
            }
        }
        return null
    }

    private fun fallbackPath(cols: Int, rows: Int, random: Random): List<Int> {
        val startCol = random.nextInt(cols - 1)
        val path = ArrayList<Int>(rows * 2)
        for (row in 0 until rows) {
            val leftFirst = row % 2 == 0
            if (leftFirst) {
                path.add(row * cols + startCol)
                path.add(row * cols + startCol + 1)
            } else {
                path.add(row * cols + startCol + 1)
                path.add(row * cols + startCol)
            }
        }
        return path
    }
}
