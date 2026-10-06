package com.riverlines.game.core.ui

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.GridLayout
import com.riverlines.game.core.config.GameConfig
import com.riverlines.game.domain.model.BoardState

class RiverBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    private val tileViews = ArrayList<PipeTileView>()
    private var cols = GameConfig.COLS
    private var rows = GameConfig.ROWS
    private var tileSize = 0
    private var tapListener: ((Int) -> Unit)? = null

    init {
        columnCount = cols
        rowCount = rows
        setUseDefaultMargins(false)
        tileSize = defaultTileSize()
    }

    fun setOnTileTapListener(listener: (Int) -> Unit) {
        tapListener = listener
    }

    fun configure(availableWidth: Int, availableHeight: Int) {
        if (availableWidth <= 0) return
        val frame = dp(FRAME_DP)
        val maxWidth = minOf(availableWidth, dp(GameConfig.BOARD_MAX_WIDTH_DP))
        val fromWidth = (maxWidth - 2 * frame) / cols
        val fromHeight = if (availableHeight > 0) {
            (availableHeight - 2 * frame) / rows
        } else {
            fromWidth
        }
        val next = minOf(fromWidth, fromHeight).coerceAtLeast(dp(28))
        if (next == tileSize) return
        tileSize = next
        applySizes()
    }

    fun bind(board: BoardState, flooded: Set<Int>, hintIndex: Int) {
        if (board.cols != cols || board.rows != rows) {
            cols = board.cols
            rows = board.rows
            columnCount = cols
            rowCount = rows
            removeAllViews()
            tileViews.clear()
        }
        ensureTiles()
        for (tile in board.tiles) {
            if (tile.index >= tileViews.size) continue
            val view = tileViews[tile.index]
            view.render(tile, flooded.contains(tile.index), tile.index == hintIndex)
        }
    }

    fun tileViewAt(index: Int): PipeTileView? = tileViews.getOrNull(index)

    fun playFlowWave(order: List<Int>) {
        for (position in order.indices) {
            val view = tileViews.getOrNull(order[position]) ?: continue
            view.pulse(position * GameConfig.FLOW_STAGGER_MS)
        }
    }

    fun cancelAnimations() {
        for (view in tileViews) {
            view.cancelAnimations()
        }
    }

    private fun ensureTiles() {
        val needed = cols * rows
        while (tileViews.size < needed) {
            val index = tileViews.size
            val view = PipeTileView(context)
            view.setOnClickListener {
                try {
                    tapListener?.invoke(index)
                } catch (e: Exception) {
                }
            }
            val params = LayoutParams(GridLayout.spec(index / cols), GridLayout.spec(index % cols))
            params.width = tileSize
            params.height = tileSize
            addView(view, params)
            tileViews.add(view)
        }
    }

    private fun applySizes() {
        for (view in tileViews) {
            val params = view.layoutParams
            if (params != null) {
                params.width = tileSize
                params.height = tileSize
                view.layoutParams = params
            }
        }
        val own = layoutParams
        if (own != null) {
            own.width = tileSize * cols
            own.height = tileSize * rows
            layoutParams = own
        }
        requestLayout()
    }

    private fun defaultTileSize(): Int {
        val frame = dp(FRAME_DP)
        val screen = resources.displayMetrics.widthPixels - dp(32)
        val maxWidth = minOf(screen, dp(GameConfig.BOARD_MAX_WIDTH_DP))
        return ((maxWidth - 2 * frame) / cols).coerceAtLeast(dp(28))
    }

    private fun dp(value: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value.toFloat(),
        resources.displayMetrics
    ).toInt()

    private companion object {
        const val FRAME_DP = 8
    }
}
