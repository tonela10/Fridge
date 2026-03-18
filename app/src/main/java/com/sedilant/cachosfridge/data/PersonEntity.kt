package com.sedilant.cachosfridge.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "people")
data class PersonEntity(
    @PrimaryKey val id: String,
    val name: String,
    val balanceCents: Int
)

