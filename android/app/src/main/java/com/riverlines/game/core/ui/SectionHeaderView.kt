package com.riverlines.game.core.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.riverlines.game.databinding.ViewSectionHeaderBinding

class SectionHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ViewSectionHeaderBinding.inflate(LayoutInflater.from(context), this)

    fun bind(title: String, subtitle: String) {
        binding.sectionTitle.text = title
        binding.sectionSubtitle.text = subtitle
        contentDescription = title + " " + subtitle
    }
}
