package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosticFailureDao {

    @Query("SELECT * FROM component_failure_risks ORDER BY dynamicRiskScore DESC")
    fun getAllFailureRisks(): Flow<List<ComponentFailureRiskEntity>>

    @Query("SELECT * FROM component_failure_risks WHERE riskSeverity IN ('CRITICAL', 'HIGH') ORDER BY dynamicRiskScore DESC")
    fun getHighPriorityRisks(): Flow<List<ComponentFailureRiskEntity>>

    @Query("SELECT * FROM component_failure_risks WHERE componentId = :componentId")
    fun getFailureRiskByComponent(componentId: String): Flow<ComponentFailureRiskEntity?>

    @Query("SELECT * FROM component_failure_risks WHERE componentId = :componentId")
    suspend fun getFailureRiskDirect(componentId: String): ComponentFailureRiskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRisks(risks: List<ComponentFailureRiskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRisk(risk: ComponentFailureRiskEntity)

    @Query("SELECT COUNT(*) FROM component_failure_risks")
    suspend fun getCount(): Int

    @Query("DELETE FROM component_failure_risks")
    suspend fun clearAllRisks()
}
