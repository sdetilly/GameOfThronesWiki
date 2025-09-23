package com.tillylabs.gameofthroneswiki.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tillylabs.gameofthroneswiki.database.entities.HouseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HouseDao {
    @Query("SELECT * FROM houses ORDER BY name ASC")
    fun getAllHouses(): Flow<List<HouseEntity>>

    @Query("SELECT * FROM houses ORDER BY name ASC LIMIT :limit OFFSET :offset")
    suspend fun getHousesPaginated(
        limit: Int,
        offset: Int,
    ): List<HouseEntity>

    @Query("SELECT COUNT(*) FROM houses")
    suspend fun getHousesCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHouses(houses: List<HouseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHouse(house: HouseEntity)

    @Query("DELETE FROM houses")
    suspend fun deleteAllHouses()

    @Query("SELECT * FROM houses WHERE url = :url")
    suspend fun getHouseByUrl(url: String): HouseEntity?

    @Query("SELECT MAX(lastUpdated) FROM houses")
    suspend fun getLastUpdatedTimestamp(): Long?
}
