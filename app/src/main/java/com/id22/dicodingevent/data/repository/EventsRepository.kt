package com.id22.dicodingevent.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.id22.dicodingevent.data.Result
import com.id22.dicodingevent.data.domain.map.eventDetailResponseToModel
import com.id22.dicodingevent.data.domain.map.eventEntityToModel
import com.id22.dicodingevent.data.domain.map.eventResponseToModel
import com.id22.dicodingevent.data.domain.model.EventDetailModel
import com.id22.dicodingevent.data.domain.model.EventModel
import com.id22.dicodingevent.data.source.local.entity.FavoriteEvent
import com.id22.dicodingevent.data.source.local.room.EventsDao
import com.id22.dicodingevent.data.source.remote.network.ApiService
import com.id22.dicodingevent.util.ErrorException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException

class EventsRepository(
    private val apiService: ApiService,
    private val eventsDao: EventsDao
) {
    fun getAllEvents(
        active: Int = -1,
        keyword: String? = null,
        limit: Int? = null
    ): LiveData<Result<List<EventModel>>> = liveData {
        emit(Result.Loading(true))

        try {
            val response = apiService.getAllEvents(active, keyword, limit)
            val events = response.listEvents ?: emptyList()

            if (keyword.isNullOrEmpty()) {
                val data = events.map { event ->
                    val isBookmarked = eventsDao.isEventsBookmarked(event.id)

                    FavoriteEvent(
                        id = event.id,
                        imageLogo = event.imageLogo,
                        name = event.name,
                        summary = event.summary,
                        active = active,
                        isBookmarked = isBookmarked
                    )
                }

                eventsDao.insertDeleteAndRefreshEvents(data, active)
            }

            emit(Result.Success(events.map { event ->
                val isBookmarked = eventsDao.isEventsBookmarked(event.id)
                eventResponseToModel(event, isBookmarked)
            }))
        } catch (e: Exception) {
            val eventException = handleException(e)
            emit(Result.Error(eventException.message))
        } finally {
            emit(Result.Loading(false))
        }

        if (keyword.isNullOrEmpty()) {
            val localData: LiveData<Result<List<EventModel>>> =
                eventsDao.getAllEvents(active).map { event ->
                    Result.Success(event.map { eventEntityToModel(it) })
                }
            emitSource(localData)
        }
    }

    fun getDetailEvent(id: Int): LiveData<Result<EventDetailModel>> = liveData {
        emit(Result.Loading(true))

        try {
            val response = apiService.getDetailEvent(id)
            val event = response.event ?: throw Exception("Event not found")

            val isBookmarked = eventsDao.isEventsBookmarked(event.id)
            val detailData = eventsDao.getEventsBookmarked(event.id)
            val data = eventDetailResponseToModel(event, isBookmarked, detailData?.active ?: -1)

            emit(Result.Success(data))
        } catch (e: Exception) {
            val eventException = handleException(e)
            emit(Result.Error(eventException.message))
        } finally {
            emit(Result.Loading(false))
        }
    }

    fun getBookmarkedEvents(): LiveData<List<EventModel>> = liveData {
        val localData: LiveData<List<EventModel>> =
            eventsDao.getBookmarkedEvents().map { event ->
                event.map { eventEntityToModel(it) }
            }
        emitSource(localData)
    }

    suspend fun setBookmarkedEvents(id: Int, bookmarkState: Boolean) {
        eventsDao.updateEvents(id, bookmarkState)
    }

    private fun handleException(e: Exception): ErrorException {
        return when (e) {
            is SocketTimeoutException -> ErrorException.TimeoutError(TIMEOUT_ERROR_MESSAGE)
            is SocketException -> ErrorException.NetworkError(NETWORK_ERROR_MESSAGE)
            is IOException -> ErrorException.NetworkError(NETWORK_ERROR_MESSAGE)
            is HttpException -> ErrorException.ServerError(
                when (e.code()) {
                    404 -> NOT_FOUND_MESSAGE
                    500 -> SERVER_ERROR_MESSAGE
                    else -> "An error occurred: ${e.localizedMessage}"
                }
            )

            else -> ErrorException.UnknownError(UNKNOWN_ERROR_MESSAGE)
        }
    }

    private companion object {
        const val NETWORK_ERROR_MESSAGE = "Internet connection is having problems!"
        const val NOT_FOUND_MESSAGE = "Data not found!"
        const val SERVER_ERROR_MESSAGE = "Server is having problems!"
        const val TIMEOUT_ERROR_MESSAGE =
            "Connection timed out. Please check your internet connection and try again."
        const val UNKNOWN_ERROR_MESSAGE = "An unknown error occurred!"
    }
}