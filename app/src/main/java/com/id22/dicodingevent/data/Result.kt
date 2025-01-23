package com.id22.dicodingevent.data

sealed class Result<out R> private constructor() {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val error: String? = null) : Result<Nothing>()
    data class Loading(val isLoading: Boolean) : Result<Nothing>()
}
