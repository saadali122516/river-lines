package com.riverlines.game.core.ui

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.ImageViewCompat
import com.riverlines.game.R
import com.riverlines.game.core.config.GameConfig
import com.riverlines.game.databinding.ViewPipeTileBinding
import com.riverlines.game.domain.model.PipeTile
import com.riverlines.game.domain.model.TileKind

class PipeTileView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewPipeTileBinding.inflate(LayoutInflater.from(context), this)

    private val dryColor = ContextCompat.getColor(context, R.color.color_pipe_dry)
    private val flowColor = ContextCompat.getColor(context, R.color.color_primary)
    private val blockedColor = ContextCompat.getColor(context, R.color.color_text_tertiary)

    init {
        isClickable = true
        isFocusable = true
        contentDescription = context.getString(R.string.cd_pipe_tile)
    }

    fun render(tile: PipeTile, flooded: Boolean, highlighted: Boolean) {
        val pipeRes = pipeDrawable(tile.kind)
        if (pipeRes == 0) {
            binding.tilePipe.visibility = View.GONE
        } else {
            binding.tilePipe.visibility = View.VISIBLE
            binding.tilePipe.setImageResource(pipeRes)
            binding.tilePipe.rotation = pipeAngle(tile)
            val tint = when {
                tile.kind == TileKind.ROCK -> blockedColor
                flooded -> flowColor
                else -> dryColor
            }
            ImageViewCompat.setImageTintList(binding.tilePipe, ColorStateList.valueOf(tint))
        }

        val nodeRes = nodeDrawable(tile.kind)
        if (nodeRes == 0) {
            binding.tileNode.visibility = View.GONE
            binding.tileNode.setImageDrawable(null)
        } else {
            binding.tileNode.visibility = View.VISIBLE
            binding.tileNode.setImageResource(nodeRes)
        }

        binding.tileBed.setBackgroundResource(
            when {
                tile.kind == TileKind.ROCK -> R.drawable.shape_tile_bed_blocked
                flooded -> R.drawable.shape_tile_bed_flow
                else -> R.drawable.shape_tile_bed
            }
        )

        binding.tileHighlight.visibility = if (highlighted) View.VISIBLE else View.GONE

        val enabled = tile.kind.rotatable
        isEnabled = enabled
        alpha = if (tile.kind == TileKind.ROCK) 0.55f else 1f

        val state = when {
            tile.kind == TileKind.ROCK -> R.string.state_blocked
            flooded -> R.string.state_flowing
            else -> R.string.state_dry
        }
        ViewCompat.setStateDescription(this, context.getString(state))
    }

    fun animateTurn() {
        try {
            animate().cancel()
            binding.tilePipe.animate().cancel()
            binding.tilePipe.animate()
                .rotationBy(90f)
                .setDuration(GameConfig.ROTATE_ANIM_MS)
                .setInterpolator(DecelerateInterpolator())
                .start()
        } catch (e: Exception) {
        }
    }

    fun pulse(delay: Long) {
        try {
            animate().cancel()
            scaleX = 1f
            scaleY = 1f
            animate()
                .scaleX(1.06f)
                .scaleY(1.06f)
                .setStartDelay(delay)
                .setDuration(140L)
                .withEndAction {
                    try {
                        if (!isAttachedToWindow) return@withEndAction
                        animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setStartDelay(0L)
                            .setDuration(160L)
                            .start()
                    } catch (e: Exception) {
                    }
                }
                .start()
        } catch (e: Exception) {
        }
    }

    fun shake() {
        try {
            animate().cancel()
            translationX = 0f
            animate()
                .translationX(6f)
                .setDuration(60L)
                .withEndAction {
                    try {
                        if (!isAttachedToWindow) return@withEndAction
                        animate()
                            .translationX(-6f)
                            .setDuration(60L)
                            .withEndAction {
                                try {
                                    if (!isAttachedToWindow) return@withEndAction
                                    animate().translationX(0f).setDuration(60L).start()
                                } catch (e: Exception) {
                                }
                            }
                            .start()
                    } catch (e: Exception) {
                    }
                }
                .start()
        } catch (e: Exception) {
        }
    }

    fun cancelAnimations() {
        try {
            animate().cancel()
            binding.tilePipe.animate().cancel()
        } catch (e: Exception) {
        }
    }

    private fun pipeAngle(tile: PipeTile): Float {
        if (tile.kind == TileKind.SPRING || tile.kind == TileKind.LAKE) {
            val opening = tile.openings.firstOrNull() ?: return 0f
            return opening.index * 90f
        }
        return (tile.rotation % 4) * 90f
    }

    private fun pipeDrawable(kind: TileKind): Int = when (kind) {
        TileKind.SPRING -> R.drawable.ic_pipe_stub
        TileKind.LAKE -> R.drawable.ic_pipe_stub
        TileKind.STRAIGHT -> R.drawable.ic_pipe_straight
        TileKind.ELBOW -> R.drawable.ic_pipe_elbow
        TileKind.WETLAND -> R.drawable.ic_pipe_elbow
        TileKind.TEE -> R.drawable.ic_pipe_tee
        TileKind.CROSS -> R.drawable.ic_pipe_cross
        else -> 0
    }

    private fun nodeDrawable(kind: TileKind): Int = when (kind) {
        TileKind.SPRING -> R.drawable.sprite_spring
        TileKind.LAKE -> R.drawable.sprite_lake
        TileKind.WETLAND -> R.drawable.sprite_wetland
        TileKind.ROCK -> R.drawable.sprite_rock
        else -> 0
    }
}
