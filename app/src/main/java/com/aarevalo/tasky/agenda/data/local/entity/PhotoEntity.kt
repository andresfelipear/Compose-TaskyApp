package com.aarevalo.tasky.agenda.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "photos",
)
data class PhotoEntity(
    @PrimaryKey(autoGenerate = false)
    val key: String,
    val uri: String,
)
