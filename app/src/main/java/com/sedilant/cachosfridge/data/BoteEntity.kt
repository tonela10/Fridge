package com.sedilant.cachosfridge.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bote")
data class BoteEntity(
    @PrimaryKey val id: Int = DEFAULT_BOTE_ID,
    val balanceCents: Int
) {
    companion object {
        const val DEFAULT_BOTE_ID = 1
    }
}

