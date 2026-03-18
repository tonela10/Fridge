package com.sedilant.cachosfridge.data

import android.content.Context
import androidx.room.Room

class AppContainer(context: Context) {
    private val db = Room.databaseBuilder(
        context,
        FridgeDatabase::class.java,
        "cachos_fridge.db"
    )
        .addMigrations(FridgeDatabase.MIGRATION_1_2)
        .fallbackToDestructiveMigration()
        .build()

    val repository: FridgeRepository = FridgeRepositoryImpl(
        db = db,
        productDao = db.productDao(),
        personDao = db.personDao(),
        boteDao = db.boteDao()
    )
}

