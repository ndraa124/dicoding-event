package com.id22.dicodingevent.di

import android.content.Context
import com.id22.dicodingevent.data.repository.EventsRepository
import com.id22.dicodingevent.data.source.local.room.EventsDatabase
import com.id22.dicodingevent.data.source.remote.network.ApiConfig
import com.id22.dicodingevent.util.SettingPreferences
import com.id22.dicodingevent.util.dataStore

object Injection {
    fun provideRepository(context: Context): EventsRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventsDatabase.getInstance(context)
        val dao = database.eventsDao()

        return EventsRepository(
            apiService = apiService,
            eventsDao = dao
        )
    }

    fun provideSettingPreferences(context: Context): SettingPreferences {
        val dataStore = context.dataStore
        return SettingPreferences.getInstance(dataStore)
    }
}
