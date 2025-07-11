package com.aarevalo.tasky.agenda.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aarevalo.tasky.agenda.data.local.entity.PendingItemSyncEntity

@Dao
interface PendingItemSyncDao {
    @Upsert
    suspend fun upsertPendingItemSyn(pendingItemSync: PendingItemSyncEntity)

    @Query("SELECT * FROM pending_item_sync")
    suspend fun getPendingItemSync(): List<PendingItemSyncEntity>

    @Query("SELECT * FROM pending_item_sync WHERE itemId = :id")
    suspend fun getPendingItemSyncById(id: String): PendingItemSyncEntity?

    @Query("DELETE FROM pending_item_sync WHERE itemId = :id")
    suspend fun deletePendingItemSyncById(id: String)

    @Query("SELECT * FROM pending_item_sync WHERE userId = :userId")
    suspend fun getPendingItemSyncByUserId(userId: String): List<PendingItemSyncEntity>

    @Query("DELETE FROM pending_item_sync")
    suspend fun deleteAllPendingItemSync()
}