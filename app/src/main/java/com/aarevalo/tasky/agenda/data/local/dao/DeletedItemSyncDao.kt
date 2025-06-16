package com.aarevalo.tasky.agenda.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aarevalo.tasky.agenda.data.local.entity.DeletedItemSyncEntity

@Dao
interface DeletedItemSyncDao {
    @Upsert
    suspend fun upsertDeletedItemSyn(deletedItemSyn: DeletedItemSyncEntity)

    @Query("SELECT * FROM deleted_item_sync WHERE userId=:userId")
    suspend fun getDeletedItemsByUserId(userId: String): List<DeletedItemSyncEntity>

    @Query("SELECT * FROM deleted_item_sync WHERE userId = :userId AND itemType = :itemType")
    suspend fun getDeletedItemsByUserIdAndType(userId: String, itemType: String): List<DeletedItemSyncEntity>

    @Query("DELETE FROM deleted_item_sync WHERE itemId=:itemId")
    suspend fun deleteDeletedItemsByItemId(itemId: String)

    @Query("DELETE FROM deleted_item_sync")
    suspend fun deleteAllDeletedItems() // New: For full table clear
}