package com.id22.dicodingevent.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.id22.dicodingevent.data.Result
import com.id22.dicodingevent.data.domain.model.EventDetailModel
import com.id22.dicodingevent.data.domain.model.EventModel
import com.id22.dicodingevent.data.repository.EventsRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventsViewModel(private val eventsRepository: EventsRepository) : ViewModel() {
    private var cachedEventList = mutableMapOf<Int, List<EventModel>>()
    private var cachedEventDetail: EventDetailModel? = null

    private val _isBookmarked = MutableLiveData<Boolean>()
    val isBookmarked: LiveData<Boolean> = _isBookmarked

    fun getAllEvents(
        active: Int = -1,
        keyword: String? = null,
        limit: Int? = null
    ): LiveData<Result<List<EventModel>>> {
        if (!keyword.isNullOrEmpty()) {
            cachedEventList.remove(active)
        }

        cachedEventList[active]?.let {
            return liveData {
                emit(Result.Loading(false))

                if (it.isEmpty()) {
                    emit(Result.Error())
                } else {
                    emit(Result.Success(it))
                }
            }
        }

        return refreshAllEventData(active, keyword, limit)
    }

    fun refreshAllEventData(
        active: Int = -1,
        keyword: String? = null,
        limit: Int? = null
    ): LiveData<Result<List<EventModel>>> {
        cachedEventList.remove(active)

        return eventsRepository.getAllEvents(active, keyword, limit).map { result ->
            if (result is Result.Success) {
                cachedEventList[active] = result.data
            }

            result
        }
    }

    fun getDetailEvent(id: Int): LiveData<Result<EventDetailModel>> {
        cachedEventDetail?.let {
            return liveData {
                emit(Result.Loading(false))
                emit(Result.Success(it))
            }
        }

        return refreshDetailEventData(id)
    }

    fun refreshDetailEventData(id: Int): LiveData<Result<EventDetailModel>> {
        cachedEventDetail = null

        return eventsRepository.getDetailEvent(id).map { result ->
            if (result is Result.Success) {
                cachedEventDetail = result.data
            }

            result
        }
    }

    fun getBookmarkedEvents() = eventsRepository.getBookmarkedEvents()

    fun saveEvent(id: Int, isBookmarked: Boolean) {
        viewModelScope.launch(Dispatchers.IO + handler) {
            if (isBookmarked) {
                eventsRepository.setBookmarkedEvents(id, false)
                _isBookmarked.postValue(false)
            } else {
                eventsRepository.setBookmarkedEvents(id, true)
                _isBookmarked.postValue(true)
            }
        }
    }

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.e("EventsViewModel", "Coroutine exception", exception)
    }
}