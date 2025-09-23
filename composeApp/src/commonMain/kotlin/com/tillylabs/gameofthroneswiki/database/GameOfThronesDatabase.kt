package com.tillylabs.gameofthroneswiki.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.tillylabs.gameofthroneswiki.database.converters.Converters
import com.tillylabs.gameofthroneswiki.database.dao.BookDao
import com.tillylabs.gameofthroneswiki.database.dao.CharacterDao
import com.tillylabs.gameofthroneswiki.database.dao.HouseDao
import com.tillylabs.gameofthroneswiki.database.entities.BookEntity
import com.tillylabs.gameofthroneswiki.database.entities.CharacterEntity
import com.tillylabs.gameofthroneswiki.database.entities.HouseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [
        BookEntity::class,
        CharacterEntity::class,
        HouseEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
@ConstructedBy(GameOfThronesDatabaseConstructor::class)
abstract class GameOfThronesDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao

    abstract fun characterDao(): CharacterDao

    abstract fun houseDao(): HouseDao
}

expect object GameOfThronesDatabaseConstructor : RoomDatabaseConstructor<GameOfThronesDatabase> {
    override fun initialize(): GameOfThronesDatabase
}

fun getRoomDatabase(builder: RoomDatabase.Builder<GameOfThronesDatabase>): GameOfThronesDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
