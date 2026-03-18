package com.sedilant.cachosfridge.data

// ProductEntity con campo que indica si tiene asset (no deletable)
data class Product(
    val id: String,
    val name: String,
    val priceCents: Int,
    val stock: Int,
    val hasAsset: Boolean = false
)

