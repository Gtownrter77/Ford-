package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OfflineCacheDao {

    // --- 3D Assets Cache ---
    @Query("SELECT * FROM cached_3d_assets ORDER BY name ASC")
    fun getAllCached3DAssets(): Flow<List<Cached3DAssetEntity>>

    @Query("SELECT * FROM cached_3d_assets WHERE id = :id LIMIT 1")
    suspend fun getCached3DAssetById(id: String): Cached3DAssetEntity?

    @Query("SELECT * FROM cached_3d_assets WHERE systemName = :systemName ORDER BY name ASC")
    fun getCached3DAssetsBySystem(systemName: String): Flow<List<Cached3DAssetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert3DAssets(assets: List<Cached3DAssetEntity>)

    @Query("DELETE FROM cached_3d_assets")
    suspend fun clear3DAssetsCache()

    @Query("SELECT COUNT(*) FROM cached_3d_assets")
    fun get3DAssetsCountFlow(): Flow<Int>

    // --- Repair Manuals Cache ---
    @Query("SELECT * FROM cached_repair_manuals ORDER BY title ASC")
    fun getAllCachedRepairManuals(): Flow<List<CachedRepairManualEntity>>

    @Query("SELECT * FROM cached_repair_manuals WHERE id = :id LIMIT 1")
    suspend fun getCachedRepairManualById(id: String): CachedRepairManualEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepairManuals(manuals: List<CachedRepairManualEntity>)

    @Query("DELETE FROM cached_repair_manuals")
    suspend fun clearRepairManualsCache()

    @Query("SELECT COUNT(*) FROM cached_repair_manuals")
    fun getRepairManualsCountFlow(): Flow<Int>

    // --- Diagnostic Symptoms Cache ---
    @Query("SELECT * FROM cached_symptoms ORDER BY title ASC")
    fun getAllCachedSymptoms(): Flow<List<CachedSymptomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymptoms(symptoms: List<CachedSymptomEntity>)

    @Query("DELETE FROM cached_symptoms")
    suspend fun clearSymptomsCache()

    @Query("SELECT COUNT(*) FROM cached_symptoms")
    fun getSymptomsCountFlow(): Flow<Int>

    // --- Cache Manifest & Version Upgradability ---
    @Query("SELECT * FROM cache_manifest WHERE manifestId = 'primary_manifest' LIMIT 1")
    fun getCacheManifestFlow(): Flow<CacheManifestEntity?>

    @Query("SELECT * FROM cache_manifest WHERE manifestId = 'primary_manifest' LIMIT 1")
    suspend fun getCacheManifest(): CacheManifestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateManifest(manifest: CacheManifestEntity)
}
