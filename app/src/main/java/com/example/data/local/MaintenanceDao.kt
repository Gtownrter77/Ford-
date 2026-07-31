package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceDao {
    @Query("SELECT * FROM maintenance_logs ORDER BY dateLoggedMillis DESC")
    fun getAllMaintenanceLogs(): Flow<List<MaintenanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: MaintenanceEntity): Long

    @Query("DELETE FROM maintenance_logs WHERE id = :logId")
    suspend fun deleteLogById(logId: Long)

    @Query("SELECT * FROM vehicle_profile WHERE id = 1")
    fun getVehicleProfile(): Flow<VehicleProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveVehicleProfile(profile: VehicleProfileEntity)
}
