package com.sedilant.cachosfridge.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

enum class TransactionType {
    PURCHASE_NOW,
    PURCHASE_CARD,
    PURCHASE_BOTE,
    ADD_FUNDS,
    ADD_BOTE,
    SETTLE_DEBT
}

class TransactionTypeConverter {
    @TypeConverter
    fun fromType(type: TransactionType): String = type.name

    @TypeConverter
    fun toType(name: String): TransactionType = TransactionType.valueOf(name)
}

@Entity(tableName = "transactions")
@TypeConverters(TransactionTypeConverter::class)
data class TransactionEntity(
    @PrimaryKey val id: String,
    val type: TransactionType,
    val amountCents: Int,
    val personId: String?,
    val personName: String?,
    val productName: String?,
    val timestampMs: Long
)
