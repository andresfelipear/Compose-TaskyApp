package com.aarevalo.tasky.agenda.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aarevalo.tasky.agenda.data.local.entity.PendingItemSyncEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingItemSyncDao {
    @Upsert
    suspend fun upsertPendingItemSyn(pendingItemSyn: PendingItemSyncDao)

    @Query("SELECT * FROM pending_item_sync")
    suspend fun getPendingItemSync(): List<PendingItemSyncDao>

    @Query("SELECT * FROM pending_item_sync WHERE itemId = :id")
    suspend fun getPendingItemSyncById(id: String): PendingItemSyncDao?

    @Query("DELETE FROM pending_item_sync WHERE itemId = :id")
    suspend fun deletePendingItemSyncById(id: String)

    @Query("SELECT * FROM pending_item_sync WHERE itemType = :type AND syncOperation = :operation")
    suspend fun getPendingItemsByTypeAndOperation(type: String, operation: String): List<PendingItemSyncEntity>

    @Query("SELECT * FROM pending_item_sync WHERE itemType = :type")
    suspend fun getPendingItemsByType(type: String): List<PendingItemSyncEntity>

    @Query("SELECT * FROM pending_item_sync WHERE syncOperation = :operation")
    suspend fun getPendingItemsByOperation(operation: String): List<PendingItemSyncEntity>

    @Query("DELETE FROM pending_item_sync WHERE itemType = :type")
    suspend fun deletePendingItemsByType(type: String)

    @Query("DELETE FROM pending_item_sync WHERE syncOperation = :operation")
    suspend fun deletePendingItemsByOperation(operation: String)

    @Query("DELETE FROM pending_item_sync WHERE itemType = :type AND syncOperation = :operation")
    suspend fun deletePendingItemsByTypeAndOperation(type: String, operation: String)

    @Query("DELETE FROM pending_item_sync")
    suspend fun deleteAllPendingItemSync()
}