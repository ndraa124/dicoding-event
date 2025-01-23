package com.id22.dicodingevent.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteEvent(
    @PrimaryKey
    val id: Int,
    val name: String,
    val imageLogo: String,
    val summary: String,
    val active: Int,
    var isBookmarked: Boolean = false
)
