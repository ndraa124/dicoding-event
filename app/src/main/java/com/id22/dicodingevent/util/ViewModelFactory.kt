package com.id22.dicodingevent.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.id22.dicodingevent.di.Injection
import com.id22.dicodingevent.viewmodel.EventsViewModel
import com.id22.dicodingevent.viewmodel.SettingsViewModel

interface ViewModelAssistedFactory<T : ViewModel> {
    fun create(): T
}

class ViewModelFactory private constructor(
    private val factories: Map<Class<out ViewModel>, ViewModelAssistedFactory<*>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val factory = factories[modelClass]
            ?: throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")

        return factory.create() as T
    }

    class Builder {
        private val factories = mutableMapOf<Class<out ViewModel>, ViewModelAssistedFactory<*>>()

        fun <T : ViewModel> add(
            modelClass: Class<T>,
            factory: ViewModelAssistedFactory<T>
        ): Builder {
            factories[modelClass] = factory
            return this
        }

        fun build(): ViewModelFactory = ViewModelFactory(factories.toMap())
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: createFactory(context).also { instance = it }
            }

        private fun createFactory(context: Context): ViewModelFactory {
            val settingPreferences = Injection.provideSettingPreferences(context)
            val eventsRepository = Injection.provideRepository(context)

            return Builder()
                .add(
                    EventsViewModel::class.java,
                    object : ViewModelAssistedFactory<EventsViewModel> {
                        override fun create() = EventsViewModel(eventsRepository)
                    }
                )
                .add(
                    SettingsViewModel::class.java,
                    object : ViewModelAssistedFactory<SettingsViewModel> {
                        override fun create() = SettingsViewModel(settingPreferences)
                    }
                )
                .build()
        }
    }
}