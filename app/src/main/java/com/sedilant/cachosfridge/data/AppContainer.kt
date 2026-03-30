package com.sedilant.cachosfridge.data

import android.content.Context
import androidx.room.Room
import com.sedilant.cachosfridge.nfc.NfcManager

class AppContainer(context: Context) {
    private val db = Room.databaseBuilder(
        context,
        FridgeDatabase::class.java,
        "cachos_fridge.db"
    )
        .addMigrations(FridgeDatabase.MIGRATION_1_2, FridgeDatabase.MIGRATION_2_3, FridgeDatabase.MIGRATION_3_4)
        .fallbackToDestructiveMigration()
        .build()

    val repository: FridgeRepository = FridgeRepositoryImpl(
        db = db,
        productDao = db.productDao(),
        personDao = db.personDao(),
        boteDao = db.boteDao(),
        transactionDao = db.transactionDao()
    )

    val nfcManager: NfcManager = NfcManager()
}
