package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceDao {
    @Query("SELECT * FROM maintenance_logs ORDER BY dateLoggedMillis DESC")
    fun getAllMaintenanceLogs(): Flow<List<MaintenanceEntity>>

    @Query("SELECT * FROM maintenance_logs WHERE title LIKE '%' || :query || '%' OR componentDescription LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%' OR systemName LIKE '%' || :query || '%' ORDER BY dateLoggedMillis DESC")
    fun searchMaintenanceLogs(query: String): Flow<List<MaintenanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: MaintenanceEntity): Long

    @Update
    suspend fun updateLog(log: MaintenanceEntity)

    @Query("DELETE FROM maintenance_logs WHERE id = :logId")
    suspend fun deleteLogById(logId: Long)

    @Query("SELECT * FROM vehicle_profile WHERE id = 1")
    fun getVehicleProfile(): Flow<VehicleProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveVehicleProfile(profile: VehicleProfileEntity)

    // Upcoming Maintenance Tasks Queries
    @Query("SELECT * FROM upcoming_tasks WHERE isCompleted = 0 ORDER BY dueDateMillis ASC, targetMileage ASC")
    fun getAllPendingUpcomingTasks(): Flow<List<UpcomingTaskEntity>>

    @Query("SELECT * FROM upcoming_tasks ORDER BY dueDateMillis ASC, targetMileage ASC")
    fun getAllUpcomingTasks(): Flow<List<UpcomingTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpcomingTask(task: UpcomingTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpcomingTasks(tasks: List<UpcomingTaskEntity>)

    @Update
    suspend fun updateUpcomingTask(task: UpcomingTaskEntity)

    @Query("DELETE FROM upcoming_tasks WHERE id = :taskId")
    suspend fun deleteUpcomingTaskById(taskId: Long)

    @Query("UPDATE upcoming_tasks SET isCompleted = 1 WHERE id = :taskId")
    suspend fun markUpcomingTaskCompleted(taskId: Long)
}

