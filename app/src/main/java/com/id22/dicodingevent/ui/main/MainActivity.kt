package com.id22.dicodingevent.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.ui.setupWithNavController
import com.id22.dicodingevent.R
import com.id22.dicodingevent.databinding.ActivityMainBinding
import com.id22.dicodingevent.ui.base.BaseActivity
import com.id22.dicodingevent.util.ViewModelFactory
import com.id22.dicodingevent.viewmodel.SettingsViewModel

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val settingViewModel: SettingsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreateBind(): ActivityMainBinding =
        ActivityMainBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeSettings()
        setupBottomNavigation()
    }

    private fun observeSettings() {
        settingViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun setupBottomNavigation() {
        val navController = findNavController(
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)!!,
        )

        bind.navView.setupWithNavController(navController)
    }
}