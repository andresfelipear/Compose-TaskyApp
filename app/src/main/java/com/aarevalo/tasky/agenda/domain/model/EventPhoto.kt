package com.aarevalo.tasky.agenda.domain.model

sealed interface EventPhoto{

    data class Remote(
        val key: String,
        val photoUrl: String
    ): EventPhoto

    data class Local(
        val key: String,
        val uriString: String
    ): EventPhoto
}
