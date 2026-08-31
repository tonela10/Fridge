package com.sedilant.cachosfridge.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val paypalPoolUrl: String? = null
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}
