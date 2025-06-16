package com.aarevalo.tasky.agenda.data.local.converter

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class PhotoListConverter {
    private val moshi = Moshi.Builder().build()
    private val listType = Types.newParameterizedType(List::class.java, String::class.java)
    private val adapter = moshi.adapter<List<String>>(listType)

    @TypeConverter
    fun fromString(value: String?): List<String>? {
        return value?.let { adapter.fromJson(it) }
    }

    @TypeConverter
    fun toString(list: List<String>?): String? {
        return list?.let { adapter.toJson(it) }
    }
}