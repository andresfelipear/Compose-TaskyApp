package com.aarevalo.tasky.agenda.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aarevalo.tasky.agenda.data.local.entity.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Upsert
    suspend fun upsertPhoto(photo: PhotoEntity)

    @Upsert
    suspend fun upsertPhotos(photos: List<PhotoEntity>)

    @Query("SELECT * FROM photos")
    fun getPhotos(): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos WHERE `key` = :key")
    suspend fun getPhotoByKey(key: String): PhotoEntity?

    @Query("SELECT * FROM photos WHERE `key` IN (:keys)")
    suspend fun getPhotosByKeys(keys: List<String>): List<PhotoEntity>

    @Query("DELETE FROM photos WHERE `key` = :key")
    suspend fun deletePhotoByKey(key: String)

    @Query("DELETE FROM photos WHERE `key` IN (:keys)")
    suspend fun deletePhotosByKeys(keys: List<String>)

    @Query("DELETE FROM photos")
    suspend fun deleteAllPhotos()
}