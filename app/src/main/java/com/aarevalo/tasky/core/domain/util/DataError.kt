package com.aarevalo.tasky.core.domain.util

sealed interface DataError: Error {
    enum class Network: DataError {
        REQUEST_TIMEOUT,
        UNAUTHORIZED,
        CONFLICT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        PAYLOAD_TOO_LARGE,
        SERVER_ERROR,
        SERIALIZATION,
        NOT_FOUND,
        UNKNOWN,
        BAD_REQUEST
    }

    enum class Local: DataError {
        DISK_FULL,
        BAD_DATA,
        UNKNOWN
    }
}