package com.aarevalo.tasky.agenda.presentation.photo_preview

sealed interface PhotoPreviewAction {
    data class GoBack(
        val photoId: String,
        val wasDeleted: Boolean
    ) : PhotoPreviewAction
}
