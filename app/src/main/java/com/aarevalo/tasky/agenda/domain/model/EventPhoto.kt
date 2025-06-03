package com.aarevalo.tasky.agenda.domain.model

sealed interface EventPhoto{

    fun key(): String = when(this){
        is Remote -> key
        is Local -> key
    }

    fun uri(): String = when(this){
        is Remote -> photoUrl
        is Local -> uriString
    }

    data class Remote(
        val key: String,
        val photoUrl: String
    ): EventPhoto

    data class Local(
        val key: String,
        val uriString: String
    ): EventPhoto
}
