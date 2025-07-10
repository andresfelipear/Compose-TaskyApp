package com.aarevalo.tasky.core.data.networking

import com.aarevalo.tasky.core.domain.util.EmptyResult
import com.aarevalo.tasky.core.domain.util.asEmptyDataResult
import com.aarevalo.tasky.core.domain.util.map

import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.domain.util.Result
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.net.UnknownHostException

/**
 * A generic helper function to make a Retrofit API call,
 * handling network exceptions and mapping HTTP responses to a Result type.
 *
 * @param apiCall The suspend lambda representing the actual Retrofit API call.
 * @param mapper A lambda to transform the successful API response body (T)
 * into the desired domain model (R).
 * @return A Result object indicating success (with R) or an error (DataError.Network).
 */
suspend fun <T, R> makeApiCall(
    apiCall: suspend () -> Response<T>, // Takes a suspend function that returns Retrofit's Response
    mapper: (T) -> R // Takes a mapper function to convert Response body to desired type
): Result<R, DataError.Network> {
    return try {
        val response = apiCall() // Execute the Retrofit API call

        // Use your existing responseToResult to handle HTTP status codes
        responseToResult(response).map { body ->
            mapper(body) // Apply the specific mapper function
        }
    } catch (e: UnknownHostException) {
        Timber.e(e, "Network Error: Unknown host. Device likely offline or host unavailable.")
        Result.Error(DataError.Network.NO_INTERNET)
    } catch (e: IOException) {
        Timber.e(e, "Network Error: I/O exception. Connection problem.")
        Result.Error(DataError.Network.NO_INTERNET)
    } catch (e: Exception) {
        // Catch any other unexpected exceptions
        Timber.e(e, "Unexpected error during API call.")
        Result.Error(DataError.Network.UNKNOWN)
    }
}

/**
 * Overload for API calls that don't return a specific data type (e.g., DELETE, POST without response body).
 * Returns EmptyResult<DataError.Network>.
 */
suspend fun <T> makeApiCall(
    apiCall: suspend () -> Response<T>
): EmptyResult<DataError.Network> {
    return try {
        val response = apiCall()
        responseToResult(response).asEmptyDataResult()
    } catch (e: UnknownHostException) {
        Timber.e(e, "Network Error: Unknown host. Device likely offline or host unavailable.")
        Result.Error(DataError.Network.NO_INTERNET)
    } catch (e: IOException) {
        Timber.e(e, "Network Error: I/O exception. Connection problem.")
        Result.Error(DataError.Network.NO_INTERNET)
    } catch (e: Exception) {
        Timber.e(e, "Unexpected error during API call.")
        Result.Error(DataError.Network.UNKNOWN)
    }
}