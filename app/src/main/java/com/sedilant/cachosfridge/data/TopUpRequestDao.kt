package com.sedilant.cachosfridge.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TopUpRequestDao {
    @Query("SELECT * FROM top_up_requests WHERE status = 'PENDING' ORDER BY createdAtMs DESC")
    fun observePending(): Flow<List<TopUpRequestEntity>>

    @Query("SELECT * FROM top_up_requests WHERE id = :requestId")
    suspend fun getById(requestId: String): TopUpRequestEntity?

    @Query("SELECT * FROM top_up_requests WHERE personId = :personId AND status = 'PENDING' LIMIT 1")
    suspend fun getPendingForPerson(personId: String): TopUpRequestEntity?

    @Insert
    suspend fun insert(request: TopUpRequestEntity)

    @Update
    suspend fun update(request: TopUpRequestEntity)
}
