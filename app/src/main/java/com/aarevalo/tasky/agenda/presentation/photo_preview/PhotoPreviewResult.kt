package com.aarevalo.tasky.agenda.presentation.photo_preview

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoPreviewResult(
    val photoId: String,
    val wasPhotoDeleted: Boolean,
) : Parcelable
