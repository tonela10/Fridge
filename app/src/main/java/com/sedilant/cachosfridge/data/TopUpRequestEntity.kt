package com.sedilant.cachosfridge.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

enum class TopUpStatus {
    PENDING,
    APPROVED,
    REJECTED
}

class TopUpStatusConverter {
    @TypeConverter
    fun fromStatus(status: TopUpStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): TopUpStatus = TopUpStatus.valueOf(value)
}

@Entity(
    tableName = "top_up_requests",
    indices = [
        Index(value = ["status", "createdAtMs"]),
        Index(value = ["personId"])
    ]
)
data class TopUpRequestEntity(
    @PrimaryKey val id: String,
    val personId: String,
    val personName: String,
    val amountCents: Int,
    val status: TopUpStatus,
    val createdAtMs: Long,
    val resolvedAtMs: Long? = null
)
