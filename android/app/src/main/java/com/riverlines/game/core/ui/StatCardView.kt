package com.riverlines.game.core.ui

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.widget.ImageViewCompat
import com.riverlines.game.databinding.ViewStatCardBinding

class StatCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewStatCardBinding.inflate(LayoutInflater.from(context), this)

    fun bind(@DrawableRes iconRes: Int, value: String, label: String, accentColor: Int) {
        binding.statIcon.setImageResource(iconRes)
        ImageViewCompat.setImageTintList(binding.statIcon, ColorStateList.valueOf(accentColor))
        binding.statValue.text = value
        binding.statValue.setTextColor(accentColor)
        binding.statLabel.text = label
        binding.statCard.setStrokeColor(ColorStateList.valueOf((accentColor and 0x00FFFFFF) or 0x55000000))
        contentDescription = label + " " + value
    }
}
