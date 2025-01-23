package com.id22.dicodingevent.data.domain.map

import com.id22.dicodingevent.data.domain.model.EventDetailModel
import com.id22.dicodingevent.data.domain.model.EventModel
import com.id22.dicodingevent.data.source.local.entity.FavoriteEvent
import com.id22.dicodingevent.data.source.remote.response.EventResponse

fun eventEntityToModel(data: FavoriteEvent): EventModel =
    EventModel(
        id = data.id,
        imageLogo = data.imageLogo,
        name = data.name,
        summary = data.summary,
        isBookmarked = data.isBookmarked
    )

fun eventResponseToModel(data: EventResponse, isBookmarked: Boolean = false): EventModel =
    EventModel(
        id = data.id,
        imageLogo = data.imageLogo,
        name = data.name,
        summary = data.summary,
        isBookmarked = isBookmarked
    )

fun eventDetailResponseToModel(
    data: EventResponse?,
    isBookmarked: Boolean = false,
    active: Int? = null
): EventDetailModel =
    EventDetailModel(
        id = data!!.id,
        name = data.name,
        summary = data.summary,
        description = data.description,
        imageLogo = data.imageLogo,
        mediaCover = data.mediaCover,
        category = data.category,
        ownerName = data.ownerName,
        cityName = data.cityName,
        quota = data.quota,
        registrants = data.registrants,
        beginTime = data.beginTime,
        endTime = data.endTime,
        link = data.link,
        isBookmarked = isBookmarked,
        active = active
    )