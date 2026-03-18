package com.sedilant.cachosfridge.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val priceCents: Int,
    val stock: Int,
    val hasAsset: Boolean = false
)

