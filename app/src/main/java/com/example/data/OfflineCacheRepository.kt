package com.example.data

import com.example.data.local.CacheManifestEntity
import com.example.data.local.Cached3DAssetEntity
import com.example.data.local.CachedRepairManualEntity
import com.example.data.local.CachedSymptomEntity
import com.example.data.local.OfflineCacheDao
import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

data class OfflineCacheStatus(
    val cached3DAssetsCount: Int = 0,
    val cachedManualsCount: Int = 0,
    val cachedSymptomsCount: Int = 0,
    val contentVersion: String = "2.4.0",
    val hasPendingUpgrade: Boolean = false,
    val lastSyncMillis: Long = 0L,
    val isDatabaseReady: Boolean = false
)

class OfflineCacheRepository(
    private val offlineCacheDao: OfflineCacheDao
) {
    val cached3DAssetsFlow: Flow<List<Cached3DAssetEntity>> = offlineCacheDao.getAllCached3DAssets()
    val cachedRepairManualsFlow: Flow<List<CachedRepairManualEntity>> = offlineCacheDao.getAllCachedRepairManuals()
    val cachedSymptomsFlow: Flow<List<CachedSymptomEntity>> = offlineCacheDao.getAllCachedSymptoms()

    val assets3DCountFlow: Flow<Int> = offlineCacheDao.get3DAssetsCountFlow()
    val manualsCountFlow: Flow<Int> = offlineCacheDao.getRepairManualsCountFlow()
    val symptomsCountFlow: Flow<Int> = offlineCacheDao.getSymptomsCountFlow()
    val manifestFlow: Flow<CacheManifestEntity?> = offlineCacheDao.getCacheManifestFlow()

    /**
     * Seeds or syncs local Room database with 3D assets and repair manuals from application registry.
     */
    suspend fun seedAndSyncOfflineCache(targetVersion: String = "2.4.0") = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()

        // 1. Convert and Cache 3D Components
        val assets3DToCache = SportTracData.components.map { model ->
            val repairStepsJsonArray = JSONArray()
            model.repairSteps.forEach { step ->
                val obj = JSONObject().apply {
                    put("stepNumber", step.stepNumber)
                    put("title", step.title)
                    put("instruction", step.instruction)
                    put("warning", step.warning ?: "")
                    put("tip", step.tip ?: "")
                }
                repairStepsJsonArray.put(obj)
            }

            val torqueSpecsJsonArray = JSONArray()
            model.torqueSpecs.forEach { ts ->
                val obj = JSONObject().apply {
                    put("fastenerName", ts.fastenerName)
                    put("torqueFtLbs", ts.torqueFtLbs)
                    put("torqueNm", ts.torqueNm)
                    put("notes", ts.notes)
                }
                torqueSpecsJsonArray.put(obj)
            }

            val fastenersJsonArray = JSONArray()
            model.fasteners.forEach { f ->
                val obj = JSONObject().apply {
                    put("name", f.name)
                    put("category", f.category.name)
                    put("quantity", f.quantity)
                    put("specOrThread", f.specOrThread)
                    put("toolRequired", f.toolRequired)
                    put("notes", f.notes)
                }
                fastenersJsonArray.put(obj)
            }

            Cached3DAssetEntity(
                id = model.id,
                name = model.name,
                systemName = model.system.displayName,
                oemPartNumber = model.oemPartNumber,
                description = model.description,
                locationDescription = model.locationDescription,
                difficulty = model.difficulty,
                estimatedTimeMinutes = model.estimatedTimeMinutes,
                verticesCount = model.vertices.size,
                facesCount = model.faces.size,
                subAssembliesCount = model.subAssemblies.size,
                serialNumber = model.serialNumber,
                manualSectionRef = model.manualSectionRef,
                repairStepsJson = repairStepsJsonArray.toString(),
                torqueSpecsJson = torqueSpecsJsonArray.toString(),
                requiredToolsCsv = model.requiredTools.joinToString(","),
                commonSymptomsCsv = model.commonSymptoms.joinToString(" | "),
                fastenersJson = fastenersJsonArray.toString(),
                assetVersion = targetVersion,
                lastCachedMillis = now,
                isOfflineAvailable = true
            )
        }
        offlineCacheDao.insert3DAssets(assets3DToCache)

        // 2. Convert and Cache Repair Manuals (Service Manual Trouble Matches)
        val manualsToCache = SportTracServiceManualDiagnostics.manualTroubleMatches.map { match ->
            val diagStepsArray = JSONArray()
            match.diagnosticVerificationSteps.forEach { step -> diagStepsArray.put(step) }

            CachedRepairManualEntity(
                id = match.id,
                title = match.title,
                serviceManualSection = match.serviceManualSection,
                tsbNumber = match.tsbNumber,
                problemSummary = match.problemSummary,
                probableCause = match.probableCause,
                obdCodesCsv = match.obdCodes.joinToString(","),
                targetComponentId = match.targetComponentId,
                urgencyLevel = match.urgencyLevel,
                matchingSymptomIdsCsv = match.matchingSymptomIds.joinToString(","),
                diagnosticStepsJson = diagStepsArray.toString(),
                difficulty = match.difficulty,
                assetVersion = targetVersion,
                lastCachedMillis = now,
                isOfflineAvailable = true
            )
        }
        offlineCacheDao.insertRepairManuals(manualsToCache)

        // 3. Convert and Cache Symptoms
        val symptomsToCache = SportTracServiceManualDiagnostics.symptomsList.map { sym ->
            CachedSymptomEntity(
                id = sym.id,
                title = sym.title,
                description = sym.description,
                systemName = sym.system.displayName,
                severity = sym.severity,
                assetVersion = targetVersion,
                lastCachedMillis = now
            )
        }
        offlineCacheDao.insertSymptoms(symptomsToCache)

        // 4. Update or Create Manifest
        val existingManifest = offlineCacheDao.getCacheManifest()
        val manifestToInsert = existingManifest?.copy(
            contentVersion = targetVersion,
            lastUpdatedMillis = now,
            updateNotes = "Content version $targetVersion loaded into Room database."
        ) ?: CacheManifestEntity(
            contentVersion = targetVersion,
            lastUpdatedMillis = now,
            updateNotes = "Initial 2004 Sport Trac CAD & Factory Manual Pack v$targetVersion"
        )
        offlineCacheDao.insertOrUpdateManifest(manifestToInsert)
    }

    /**
     * Checks if an upgrade/update is available in the manifest repository.
     */
    suspend fun checkForUpgrades(): Boolean = withContext(Dispatchers.IO) {
        val manifest = offlineCacheDao.getCacheManifest()
        val currentVersion = manifest?.contentVersion ?: "2.4.0"
        val latestVersion = manifest?.latestAvailableVersion ?: "2.5.0"
        
        val isUpgradeAvailable = currentVersion != latestVersion
        if (isUpgradeAvailable && manifest != null) {
            offlineCacheDao.insertOrUpdateManifest(manifest.copy(hasPendingUpgrade = true))
        }
        return@withContext isUpgradeAvailable
    }

    /**
     * Executes content & database upgrade to latest version (e.g. v2.5.0).
     */
    suspend fun performUpgradeToLatestVersion(): String = withContext(Dispatchers.IO) {
        val manifest = offlineCacheDao.getCacheManifest()
        val targetVersion = manifest?.latestAvailableVersion ?: "2.5.0"

        // Perform seed and sync with new target version
        seedAndSyncOfflineCache(targetVersion = targetVersion)

        val updatedManifest = (offlineCacheDao.getCacheManifest() ?: CacheManifestEntity()).copy(
            contentVersion = targetVersion,
            hasPendingUpgrade = false,
            updateNotes = "Upgraded to 2004 Explorer Sport Trac Factory Service Pack v$targetVersion with updated CAD geometries and torque specs.",
            lastUpdatedMillis = System.currentTimeMillis()
        )
        offlineCacheDao.insertOrUpdateManifest(updatedManifest)
        return@withContext targetVersion
    }

    /**
     * Clears all cached Room tables for testing or resetting offline storage.
     */
    suspend fun clearAllOfflineCache() = withContext(Dispatchers.IO) {
        offlineCacheDao.clear3DAssetsCache()
        offlineCacheDao.clearRepairManualsCache()
        offlineCacheDao.clearSymptomsCache()
        
        val manifest = offlineCacheDao.getCacheManifest()
        if (manifest != null) {
            offlineCacheDao.insertOrUpdateManifest(manifest.copy(
                contentVersion = "None",
                updateNotes = "Cache cleared by user."
            ))
        }
    }
}
