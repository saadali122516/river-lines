package com.riverlines.game.presentation.splash

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.riverlines.game.databinding.FragmentSplashBinding

class SplashAnimator(private val binding: FragmentSplashBinding) {

    private val animators = ArrayList<Animator>()
    private var started = false

    fun start() {
        if (started) return
        started = true
        prepareInitialState()
        animateBadgeEntrance()
        animateRings()
        animateTitle()
        animateSubtitle()
        animateDivider()
        animateLoadingLabel()
    }

    fun cancel() {
        for (animator in animators) {
            try {
                animator.cancel()
            } catch (e: Exception) {
            }
        }
        animators.clear()
        cancelViewAnimations(
            binding.splashBadge,
            binding.splashMark,
            binding.splashTitle,
            binding.splashSubtitle,
            binding.splashDivider,
            binding.splashLoading,
            binding.splashRingOne,
            binding.splashRingTwo,
            binding.splashRingThree
        )
        started = false
    }

    private fun prepareInitialState() {
        binding.splashBadge.scaleX = ENTRY_SCALE
        binding.splashBadge.scaleY = ENTRY_SCALE
        binding.splashMark.scaleX = ENTRY_SCALE
        binding.splashMark.scaleY = ENTRY_SCALE
        binding.splashTitle.alpha = 0f
        binding.splashTitle.translationY = TITLE_OFFSET
        binding.splashSubtitle.alpha = 0f
        binding.splashDivider.alpha = 0f
        binding.splashDivider.scaleX = 0.2f
        binding.splashLoading.alpha = 0f
        for (ring in rings()) {
            ring.alpha = 0f
            ring.scaleX = 1f
            ring.scaleY = 1f
        }
    }

    private fun animateBadgeEntrance() {
        val entrance = AnimatorSet()
        entrance.playTogether(
            ObjectAnimator.ofFloat(binding.splashBadge, View.SCALE_X, ENTRY_SCALE, 1f),
            ObjectAnimator.ofFloat(binding.splashBadge, View.SCALE_Y, ENTRY_SCALE, 1f),
            ObjectAnimator.ofFloat(binding.splashMark, View.SCALE_X, ENTRY_SCALE, 1f),
            ObjectAnimator.ofFloat(binding.splashMark, View.SCALE_Y, ENTRY_SCALE, 1f)
        )
        entrance.duration = ENTRANCE_MS
        entrance.interpolator = DecelerateInterpolator()
        entrance.start()
        animators.add(entrance)

        val breathX = ObjectAnimator.ofFloat(binding.splashBadge, View.SCALE_X, 1f, BREATH_SCALE)
        val breathY = ObjectAnimator.ofFloat(binding.splashBadge, View.SCALE_Y, 1f, BREATH_SCALE)
        for (animator in listOf(breathX, breathY)) {
            animator.duration = BREATH_MS
            animator.startDelay = ENTRANCE_MS
            animator.repeatCount = ValueAnimator.INFINITE
            animator.repeatMode = ValueAnimator.REVERSE
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.start()
            animators.add(animator)
        }
    }

    private fun animateRings() {
        val delays = longArrayOf(0L, 730L, 1460L)
        val ringViews = rings()
        for (index in ringViews.indices) {
            val ring = ringViews[index]
            val scaleX = ObjectAnimator.ofFloat(ring, View.SCALE_X, 1f, RING_SCALE)
            val scaleY = ObjectAnimator.ofFloat(ring, View.SCALE_Y, 1f, RING_SCALE)
            val fade = ObjectAnimator.ofFloat(ring, View.ALPHA, RING_ALPHA, 0f)
            for (animator in listOf(scaleX, scaleY, fade)) {
                animator.duration = RING_MS
                animator.startDelay = delays[index % delays.size]
                animator.repeatCount = ValueAnimator.INFINITE
                animator.repeatMode = ValueAnimator.RESTART
                animator.interpolator = LinearInterpolator()
                animator.start()
                animators.add(animator)
            }
        }
    }

    private fun animateTitle() {
        val fade = ObjectAnimator.ofFloat(binding.splashTitle, View.ALPHA, 0f, 1f)
        val rise = ObjectAnimator.ofFloat(binding.splashTitle, View.TRANSLATION_Y, TITLE_OFFSET, 0f)
        val set = AnimatorSet()
        set.playTogether(fade, rise)
        set.duration = TITLE_MS
        set.startDelay = TITLE_DELAY
        set.interpolator = DecelerateInterpolator()
        set.start()
        animators.add(set)
    }

    private fun animateDivider() {
        val fade = ObjectAnimator.ofFloat(binding.splashDivider, View.ALPHA, 0f, 1f)
        val stretch = ObjectAnimator.ofFloat(binding.splashDivider, View.SCALE_X, 0.2f, 1f)
        val set = AnimatorSet()
        set.playTogether(fade, stretch)
        set.duration = DIVIDER_MS
        set.startDelay = DIVIDER_DELAY
        set.interpolator = DecelerateInterpolator()
        set.start()
        animators.add(set)
    }

    private fun animateSubtitle() {
        val fade = ObjectAnimator.ofFloat(binding.splashSubtitle, View.ALPHA, 0f, 1f)
        fade.duration = SUBTITLE_MS
        fade.startDelay = SUBTITLE_DELAY
        fade.interpolator = DecelerateInterpolator()
        fade.start()
        animators.add(fade)
    }

    private fun animateLoadingLabel() {
        val fade = ObjectAnimator.ofFloat(binding.splashLoading, View.ALPHA, 0f, 1f)
        fade.duration = LABEL_MS
        fade.startDelay = LABEL_DELAY
        fade.start()
        animators.add(fade)

        val pulse = ObjectAnimator.ofFloat(binding.splashLoading, View.ALPHA, 1f, LABEL_DIM_ALPHA)
        pulse.duration = LABEL_PULSE_MS
        pulse.startDelay = LABEL_DELAY + LABEL_MS
        pulse.repeatCount = ValueAnimator.INFINITE
        pulse.repeatMode = ValueAnimator.REVERSE
        pulse.interpolator = AccelerateDecelerateInterpolator()
        pulse.start()
        animators.add(pulse)
    }

    private fun rings(): List<View> = listOf(
        binding.splashRingOne,
        binding.splashRingTwo,
        binding.splashRingThree
    )

    private fun cancelViewAnimations(vararg views: View) {
        for (view in views) {
            try {
                view.animate().cancel()
                view.clearAnimation()
            } catch (e: Exception) {
            }
        }
    }

    private companion object {
        const val ENTRY_SCALE = 0.86f
        const val BREATH_SCALE = 1.045f
        const val RING_SCALE = 2.2f
        const val RING_ALPHA = 0.55f
        const val TITLE_OFFSET = 56f
        const val LABEL_DIM_ALPHA = 0.35f
        const val ENTRANCE_MS = 520L
        const val BREATH_MS = 1800L
        const val RING_MS = 2200L
        const val TITLE_MS = 620L
        const val TITLE_DELAY = 260L
        const val DIVIDER_MS = 480L
        const val DIVIDER_DELAY = 420L
        const val SUBTITLE_MS = 520L
        const val SUBTITLE_DELAY = 520L
        const val LABEL_MS = 480L
        const val LABEL_DELAY = 700L
        const val LABEL_PULSE_MS = 900L
    }
}
