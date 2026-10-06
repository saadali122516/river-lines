package com.riverlines.game.core.navigation

import com.riverlines.game.R

enum class NavTransition(
    val enter: Int,
    val exit: Int,
    val popEnter: Int,
    val popExit: Int
) {
    FADE(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out),
    FORWARD(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
}
