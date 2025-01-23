package com.id22.dicodingevent.data.source.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.id22.dicodingevent.data.source.local.entity.FavoriteEvent

@Dao
interface EventsDao {
    @Query("SELECT * FROM favoriteevent WHERE active = :active OR active = -1 ORDER BY id DESC")
    fun getAllEvents(active: Int): LiveData<List<FavoriteEvent>>

    @Query("SELECT * FROM favoriteevent where isBookmarked = 1")
    fun getBookmarkedEvents(): LiveData<List<FavoriteEvent>>

    @Query("SELECT * FROM favoriteevent WHERE id = :id LIMIT 1")
    suspend fun getEventsBookmarked(id: Int): FavoriteEvent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(event: List<FavoriteEvent>)

    @Query("UPDATE favoriteevent SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateEvents(id: Int, isBookmarked: Boolean)

    @Query("DELETE FROM favoriteevent WHERE active = :active AND isBookmarked = 0")
    suspend fun deleteAll(active: Int)

    @Query("SELECT EXISTS(SELECT * FROM favoriteevent WHERE id = :id AND isBookmarked = 1)")
    suspend fun isEventsBookmarked(id: Int): Boolean

    @Transaction
    suspend fun insertDeleteAndRefreshEvents(events: List<FavoriteEvent>, active: Int) {
        deleteAll(active)
        insertEvents(events)
    }
}
