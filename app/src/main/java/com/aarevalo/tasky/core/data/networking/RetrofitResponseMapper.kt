package com.aarevalo.tasky.core.data.networking

import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.Result
import retrofit2.Response


fun <T> responseToResult(response: Response<T>): Result<T, DataError.Network> {
    return when {
        response.isSuccessful -> {
            response.body()?.let {
                Result.Success(it)
            } ?: Result.Error(DataError.Network.UNKNOWN) // Successful but no body
        }
        response.code() == 400 -> Result.Error(DataError.Network.BAD_REQUEST)
        response.code() == 401 -> Result.Error(DataError.Network.UNAUTHORIZED)
        response.code() == 404 -> Result.Error(DataError.Network.NOT_FOUND)
        response.code() == 408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
        response.code() == 409 -> Result.Error(DataError.Network.CONFLICT)
        response.code() == 413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
        response.code() == 429 -> Result.Error(DataError.Network.TOO_MANY_REQUESTS)
        response.code() in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
        else -> Result.Error(DataError.Network.UNKNOWN)
    }
}