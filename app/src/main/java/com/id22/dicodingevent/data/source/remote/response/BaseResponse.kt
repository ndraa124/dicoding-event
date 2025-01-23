package com.id22.dicodingevent.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listEvents")
    val listEvents: List<T>? = null,

    @field:SerializedName("event")
    val event: T? = null,
)