package com.id22.dicodingevent.util

sealed class ErrorException(override val message: String) : Exception(message) {
    class NetworkError(message: String) : ErrorException(message)
    class ServerError(message: String) : ErrorException(message)
    class TimeoutError(message: String) : ErrorException(message)
    class UnknownError(message: String) : ErrorException(message)
}