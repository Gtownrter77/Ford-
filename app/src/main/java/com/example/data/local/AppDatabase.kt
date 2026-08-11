package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        MaintenanceEntity::class,
        VehicleProfileEntity::class,
        AcousticReferenceEntity::class,
        RepairChecklistEntity::class,
        UpcomingTaskEntity::class,
        Cached3DAssetEntity::class,
        CachedRepairManualEntity::class,
        CachedSymptomEntity::class,
        CacheManifestEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun maintenanceDao(): MaintenanceDao
    abstract fun acousticReferenceDao(): AcousticReferenceDao
    abstract fun repairChecklistDao(): RepairChecklistDao
    abstract fun offlineCacheDao(): OfflineCacheDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sport_trac_2004_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
