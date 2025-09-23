package com.tillylabs.gameofthroneswiki.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tillylabs.gameofthroneswiki.database.entities.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters WHERE name != '' ORDER BY name ASC")
    fun getAllCharacters(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE name != '' ORDER BY name ASC LIMIT :limit OFFSET :offset")
    suspend fun getCharactersPaginated(
        limit: Int,
        offset: Int,
    ): List<CharacterEntity>

    @Query("SELECT COUNT(*) FROM characters WHERE name != ''")
    suspend fun getCharactersCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity)

    @Query("DELETE FROM characters")
    suspend fun deleteAllCharacters()

    @Query("SELECT * FROM characters WHERE url = :url")
    suspend fun getCharacterByUrl(url: String): CharacterEntity?

    @Query("SELECT MAX(lastUpdated) FROM characters")
    suspend fun getLastUpdatedTimestamp(): Long?
}
