package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RepairChecklistDao {
    @Query("SELECT * FROM repair_checklists ORDER BY lastUpdated DESC")
    fun getAllChecklists(): Flow<List<RepairChecklistEntity>>

    @Query("SELECT * FROM repair_checklists WHERE componentId = :componentId LIMIT 1")
    fun getChecklistForComponent(componentId: String): Flow<RepairChecklistEntity?>

    @Query("SELECT * FROM repair_checklists WHERE componentId = :componentId LIMIT 1")
    suspend fun getChecklistForComponentDirect(componentId: String): RepairChecklistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveChecklist(checklist: RepairChecklistEntity)

    @Query("DELETE FROM repair_checklists WHERE componentId = :componentId")
    suspend fun deleteChecklist(componentId: String)

    @Query("DELETE FROM repair_checklists")
    suspend fun clearAllChecklists()
}
