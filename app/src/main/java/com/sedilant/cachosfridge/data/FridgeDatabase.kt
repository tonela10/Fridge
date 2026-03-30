package com.sedilant.cachosfridge.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ProductEntity::class, PersonEntity::class, BoteEntity::class, TransactionEntity::class],
    version = 4,
    exportSchema = false
)
@androidx.room.TypeConverters(TransactionTypeConverter::class)
abstract class FridgeDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun personDao(): PersonDao
    abstract fun boteDao(): BoteDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        /** v1 → v2: adds hasAsset column (default 0 = false) */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val cursor = db.query("PRAGMA table_info(products)")
                var columnExists = false
                while (cursor.moveToNext()) {
                    val idx = cursor.getColumnIndex("name")
                    if (idx != -1 && cursor.getString(idx) == "hasAsset") {
                        columnExists = true
                        break
                    }
                }
                cursor.close()
                if (!columnExists) {
                    db.execSQL(
                        "ALTER TABLE products ADD COLUMN hasAsset INTEGER NOT NULL DEFAULT 0"
                    )
                }
            }
        }

        /** v2 → v3: adds nfcCardId column to people table */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val cursor = db.query("PRAGMA table_info(people)")
                var columnExists = false
                while (cursor.moveToNext()) {
                    val idx = cursor.getColumnIndex("name")
                    if (idx != -1 && cursor.getString(idx) == "nfcCardId") {
                        columnExists = true
                        break
                    }
                }
                cursor.close()
                if (!columnExists) {
                    db.execSQL("ALTER TABLE people ADD COLUMN nfcCardId TEXT")
                }
            }
        }

        /** v3 → v4: creates transactions table */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS transactions (
                        id TEXT NOT NULL PRIMARY KEY,
                        type TEXT NOT NULL,
                        amountCents INTEGER NOT NULL,
                        personId TEXT,
                        personName TEXT,
                        productName TEXT,
                        timestampMs INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
