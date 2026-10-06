package com.riverlines.game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.riverlines.game.core.di.ServiceLocator
import com.riverlines.game.core.navigation.NavTransition
import com.riverlines.game.core.navigation.Navigator
import com.riverlines.game.databinding.ActivityMainBinding
import com.riverlines.game.presentation.splash.SplashFragment

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null

    lateinit var navigator: Navigator
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceLocator.init(applicationContext)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        _binding = binding
        setContentView(binding.root)
        navigator = Navigator(supportFragmentManager, R.id.fragment_container)
        if (savedInstanceState == null) {
            navigator.setRoot(SplashFragment(), TAG_SPLASH, NavTransition.FADE)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private companion object {
        const val TAG_SPLASH = "splash"
    }
}
