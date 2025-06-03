package com.aarevalo.tasky.core.navigation

import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    sealed interface Graph : Destination {
        @Serializable
        data object AuthGraph : Graph
        @Serializable
        data object AgendaGraph : Graph
    }

    sealed interface Route : Destination {
        @Serializable
        data object LoginRoute : Route
        @Serializable
        data object RegisterRoute : Route
        @Serializable
        data object AgendaRoute : Route
        @Serializable
        data object TaskDetailRoute : Route
        @Serializable
        data object ReminderDetailRoute : Route
        @Serializable
        data object EditTextRoute : Route
        @Serializable
        data object EventDetailRoute : Route
        @Serializable
        data class PhotoPreviewRoute(
            val photoUri: String,
            val photoKey: String,
            val isEditable: Boolean
        ) : Route
    }
}