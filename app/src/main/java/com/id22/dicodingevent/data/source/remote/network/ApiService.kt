package com.id22.dicodingevent.data.source.remote.network

import com.id22.dicodingevent.data.source.remote.response.BaseResponse
import com.id22.dicodingevent.data.source.remote.response.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    suspend fun getAllEvents(
        @Query("active") active: Int? = null,
        @Query("q") keyword: String? = null,
        @Query("limit") limit: Int? = null,
    ): BaseResponse<EventResponse>

    @GET("events/{id}")
    suspend fun getDetailEvent(
        @Path("id") id: Int
    ): BaseResponse<EventResponse>

    @GET("events")
    fun getReminderEvents(
        @Query("active") active: Int,
        @Query("limit") limit: Int,
    ): Call<BaseResponse<EventResponse>>
}
