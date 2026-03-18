package com.sedilant.cachosfridge.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BoteDao {
    @Query("SELECT * FROM bote WHERE id = :boteId")
    fun observeBote(boteId: Int = BoteEntity.DEFAULT_BOTE_ID): Flow<BoteEntity?>

    @Query("SELECT * FROM bote WHERE id = :boteId")
    suspend fun getBote(boteId: Int = BoteEntity.DEFAULT_BOTE_ID): BoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBote(bote: BoteEntity)
}

