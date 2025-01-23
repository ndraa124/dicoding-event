package com.id22.dicodingevent.data.domain.model

data class EventModel(
    val id: Int,
    val imageLogo: String,
    val name: String,
    val summary: String,
    val isBookmarked: Boolean = false
)
