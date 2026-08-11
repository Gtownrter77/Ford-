package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AcousticReferenceDao {

    @Query("SELECT * FROM acoustic_reference_sounds ORDER BY matchConfidencePercent DESC")
    fun getAllReferenceSounds(): Flow<List<AcousticReferenceEntity>>

    @Query("SELECT * FROM acoustic_reference_sounds WHERE matchCategory = :category ORDER BY matchConfidencePercent DESC")
    fun getSoundsByCategory(category: String): Flow<List<AcousticReferenceEntity>>

    @Query("SELECT * FROM acoustic_reference_sounds WHERE soundProfileId = :id LIMIT 1")
    suspend fun getSoundById(id: String): AcousticReferenceEntity?

    @Query("SELECT * FROM acoustic_reference_sounds WHERE title LIKE '%' || :query || '%' OR rootCause LIKE '%' || :query || '%'")
    fun searchSounds(query: String): Flow<List<AcousticReferenceEntity>>

    @Query("SELECT COUNT(*) FROM acoustic_reference_sounds")
    fun getSoundCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferenceSound(sound: AcousticReferenceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sounds: List<AcousticReferenceEntity>)
}
