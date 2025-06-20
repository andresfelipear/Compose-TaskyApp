package com.aarevalo.tasky.core.presentation.ui

import com.aarevalo.tasky.R
import com.aarevalo.tasky.core.domain.util.DataError
import com.aarevalo.tasky.core.presentation.util.UiText

fun DataError.asUiText(): UiText {
    return when(this) {
        DataError.Local.DISK_FULL -> UiText.StringResource(
            R.string.error_disk_full
        )
        DataError.Network.REQUEST_TIMEOUT -> UiText.StringResource(
            R.string.error_request_timeout
        )
        DataError.Network.TOO_MANY_REQUESTS -> UiText.StringResource(
            R.string.error_too_many_requests
        )
        DataError.Network.NO_INTERNET -> UiText.StringResource(
            R.string.error_no_internet
        )
        DataError.Network.PAYLOAD_TOO_LARGE -> UiText.StringResource(
            R.string.error_payload_too_large
        )
        DataError.Network.SERVER_ERROR -> UiText.StringResource(
            R.string.error_server_error
        )
        DataError.Network.SERIALIZATION -> UiText.StringResource(
            R.string.error_serialization
        )
        DataError.Network.NOT_FOUND -> UiText.StringResource(
            R.string.error_not_found
        )
        DataError.Network.BAD_REQUEST -> UiText.StringResource(
            R.string.error_bad_request
        )
        DataError.Network.UNAUTHORIZED -> UiText.StringResource(
            R.string.error_unauthorized
        )
        else -> UiText.StringResource(R.string.error_unknown)
    }
}