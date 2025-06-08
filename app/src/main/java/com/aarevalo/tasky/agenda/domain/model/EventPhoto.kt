package com.aarevalo.tasky.agenda.domain.model

sealed class EventPhoto(
    open val key: String,
    val uri: String
){

    data class Remote(
        override val key: String,
        val photoUrl: String
    ): EventPhoto(key, photoUrl)

    data class Local(
        override val key: String,
        val uriString: String
    ): EventPhoto(key, uriString)
}
