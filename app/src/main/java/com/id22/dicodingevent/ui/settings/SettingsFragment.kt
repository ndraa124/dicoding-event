package com.id22.dicodingevent.ui.settings

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.id22.dicodingevent.ReminderWorker
import com.id22.dicodingevent.databinding.FragmentSettingsBinding
import com.id22.dicodingevent.ui.base.BaseFragment
import com.id22.dicodingevent.util.ViewModelFactory
import com.id22.dicodingevent.viewmodel.SettingsViewModel
import java.util.concurrent.TimeUnit

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    private var workManager: WorkManager? = null
    private var periodicWorkRequest: PeriodicWorkRequest? = null

    private val viewModel: SettingsViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(
                    requireContext(),
                    "Notifications permission granted",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Notifications permission rejected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun createLayout(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding =
        FragmentSettingsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity !== null) {
            setupWorkManager()
            setupPeriodicWorkRequest()
            setupListeners()
            observeSettings()
        }
    }

    private fun setupWorkManager() {
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        workManager = WorkManager.getInstance(requireContext())
    }

    private fun setupPeriodicWorkRequest() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        periodicWorkRequest =
            PeriodicWorkRequest.Builder(ReminderWorker::class.java, 1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .addTag(REMINDER_TAG)
                .build()
    }

    private fun observeSettings() = with(bind) {
        viewModel.getThemeSettings().observe(viewLifecycleOwner) { isActive: Boolean ->
            switchTheme.isChecked = isActive

            if (isActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        viewModel.getDailyReminder().observe(viewLifecycleOwner) { isActive: Boolean ->
            switchDailyReminder.isChecked = isActive

            if (!isActive) {
                workManager?.cancelAllWorkByTag(REMINDER_TAG)
            }
        }
    }

    private fun setupListeners() = with(bind) {
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveThemeSetting(isChecked)
        }

        switchDailyReminder.setOnClickListener {
            viewModel.saveDailyReminderSetting(switchDailyReminder.isChecked)

            if (switchDailyReminder.isChecked) {
                startPeriodicReminderTask()
            }
        }
    }

    private fun startPeriodicReminderTask() {
        workManager?.enqueue(periodicWorkRequest!!)
    }

    companion object {
        const val REMINDER_TAG = "reminder_tag"
    }
}