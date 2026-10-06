package com.riverlines.game.core.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class Navigator(
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) {

    fun setRoot(fragment: Fragment, tag: String, transition: NavTransition) {
        if (fragmentManager.isStateSaved) return
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                transition.enter,
                transition.exit,
                transition.popEnter,
                transition.popExit
            )
            .replace(containerId, fragment, tag)
            .commit()
    }

    fun push(fragment: Fragment, tag: String, transition: NavTransition) {
        if (fragmentManager.isStateSaved) return
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                transition.enter,
                transition.exit,
                transition.popEnter,
                transition.popExit
            )
            .replace(containerId, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    fun swap(
        popTag: String,
        fragment: Fragment,
        tag: String,
        transition: NavTransition
    ) {
        if (fragmentManager.isStateSaved) return
        fragmentManager.popBackStack(popTag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                transition.enter,
                transition.exit,
                transition.popEnter,
                transition.popExit
            )
            .replace(containerId, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    fun popToRoot() {
        if (fragmentManager.isStateSaved) return
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    companion object {
        const val TAG_MENU = "menu"
        const val TAG_GAME = "game"
        const val TAG_RESULT = "result"
    }
}
