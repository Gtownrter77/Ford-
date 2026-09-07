package com.example.data

import com.example.data.local.RepairChecklistDao
import com.example.data.local.RepairChecklistEntity
import kotlinx.coroutines.flow.Flow

class RepairChecklistRepository(private val dao: RepairChecklistDao) {

    val allSavedChecklists: Flow<List<RepairChecklistEntity>> = dao.getAllChecklists()

    fun getChecklistForComponent(componentId: String): Flow<RepairChecklistEntity?> {
        return dao.getChecklistForComponent(componentId)
    }

    suspend fun getChecklistForComponentDirect(componentId: String): RepairChecklistEntity? {
        return dao.getChecklistForComponentDirect(componentId)
    }

    suspend fun saveProgress(
        componentId: String,
        componentName: String,
        currentStepIndex: Int,
        completedIndices: Set<Int>,
        totalSteps: Int
    ) {
        val completedCsv = completedIndices.sorted().joinToString(",")
        val isCompleted = completedIndices.size >= totalSteps && totalSteps > 0
        val entity = RepairChecklistEntity(
            componentId = componentId,
            componentName = componentName,
            currentStepIndex = currentStepIndex,
            completedStepsCsv = completedCsv,
            totalSteps = totalSteps,
            lastUpdated = System.currentTimeMillis(),
            isCompleted = isCompleted
        )
        dao.saveChecklist(entity)
    }

    suspend fun resetProgress(componentId: String) {
        dao.deleteChecklist(componentId)
    }

    suspend fun clearAll() {
        dao.clearAllChecklists()
    }
}
