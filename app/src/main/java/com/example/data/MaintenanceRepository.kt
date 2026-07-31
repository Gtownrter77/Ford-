package com.example.data

import com.example.data.local.MaintenanceDao
import com.example.data.local.MaintenanceEntity
import com.example.data.local.VehicleProfileEntity
import com.example.model.VehicleSystem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class MaintenanceRepository(private val maintenanceDao: MaintenanceDao) {

    val allLogs: Flow<List<MaintenanceEntity>> = maintenanceDao.getAllMaintenanceLogs()
    val vehicleProfile: Flow<VehicleProfileEntity?> = maintenanceDao.getVehicleProfile()

    suspend fun logMaintenance(log: MaintenanceEntity): Long {
        return maintenanceDao.insertLog(log)
    }

    suspend fun deleteLog(id: Long) {
        maintenanceDao.deleteLogById(id)
    }

    suspend fun updateVehicleMileage(newMileage: Int) {
        val existing = vehicleProfile.firstOrNull() ?: VehicleProfileEntity()
        maintenanceDao.saveVehicleProfile(existing.copy(currentMileage = newMileage))
    }

    suspend fun initializeDefaultDataIfEmpty() {
        val existingProfile = vehicleProfile.firstOrNull()
        if (existingProfile == null) {
            maintenanceDao.saveVehicleProfile(VehicleProfileEntity())
        }
        val existingLogs = allLogs.firstOrNull()
        if (existingLogs.isNullOrEmpty()) {
            for (sampleLog in SportTracData.initialSampleLogs) {
                maintenanceDao.insertLog(sampleLog)
            }
        }
    }
}
