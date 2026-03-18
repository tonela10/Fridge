package com.sedilant.cachosfridge.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM people ORDER BY name")
    fun observePeople(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM people WHERE id = :personId")
    suspend fun getPerson(personId: String): PersonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(people: List<PersonEntity>)

    @Update
    suspend fun updatePerson(person: PersonEntity)
}

