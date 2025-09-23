package com.tillylabs.gameofthroneswiki.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<GameOfThronesDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("my_room.db")
    return Room.databaseBuilder<GameOfThronesDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}
