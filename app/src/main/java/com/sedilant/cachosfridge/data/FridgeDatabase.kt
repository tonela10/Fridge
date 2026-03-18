package com.sedilant.cachosfridge.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ProductEntity::class, PersonEntity::class, BoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FridgeDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun personDao(): PersonDao
    abstract fun boteDao(): BoteDao
}

