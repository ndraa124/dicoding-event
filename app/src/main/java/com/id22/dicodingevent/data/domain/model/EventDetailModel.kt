package com.id22.dicodingevent.data.domain.model

data class EventDetailModel(
    val id: Int,
    val name: String,
    val summary: String,
    val description: String,
    val imageLogo: String,
    val mediaCover: String,
    val category: String,
    val ownerName: String,
    val cityName: String,
    val quota: Int,
    val registrants: Int,
    val beginTime: String,
    val endTime: String,
    val link: String,
    val isBookmarked: Boolean = false,
    val active: Int? = null
)
