package com.sedilant.cachosfridge.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ProductEntity::class,
        PersonEntity::class,
        BoteEntity::class,
        TransactionEntity::class,
        AppSettingsEntity::class,
        TopUpRequestEntity::class
    ],
    version = 5,
    exportSchema = false
)
@androidx.room.TypeConverters(TransactionTypeConverter::class, TopUpStatusConverter::class)
abstract class FridgeDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun personDao(): PersonDao
    abstract fun boteDao(): BoteDao
    abstract fun transactionDao(): TransactionDao
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun topUpRequestDao(): TopUpRequestDao

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

        /** v4 → v5: adds PayPal Pool settings and manually approved top-up requests. */
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS app_settings (
                        id INTEGER NOT NULL PRIMARY KEY,
                        paypalPoolUrl TEXT
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS top_up_requests (
                        id TEXT NOT NULL PRIMARY KEY,
                        personId TEXT NOT NULL,
                        personName TEXT NOT NULL,
                        amountCents INTEGER NOT NULL,
                        status TEXT NOT NULL,
                        createdAtMs INTEGER NOT NULL,
                        resolvedAtMs INTEGER
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_top_up_requests_status_createdAtMs " +
                        "ON top_up_requests (status, createdAtMs)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_top_up_requests_personId " +
                        "ON top_up_requests (personId)"
                )
            }
        }
    }
}
