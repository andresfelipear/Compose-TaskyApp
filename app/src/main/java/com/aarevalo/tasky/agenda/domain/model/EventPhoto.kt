package com.aarevalo.tasky.agenda.domain.model

sealed interface EventPhoto{
    val id: String

    data class Remote(
        override val id: String,
        val url: String
    ): EventPhoto

    data class Local(
        override val id: String,
        val uri: String
    ): EventPhoto
}
