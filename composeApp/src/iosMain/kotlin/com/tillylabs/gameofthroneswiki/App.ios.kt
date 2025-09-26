package com.tillylabs.gameofthroneswiki

import androidx.room.RoomDatabase
import com.tillylabs.gameofthroneswiki.database.GameOfThronesDatabase
import com.tillylabs.gameofthroneswiki.database.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun getPlatformModule(): Module =
    module {
        single<RoomDatabase.Builder<GameOfThronesDatabase>> {
            getDatabaseBuilder()
        }
    }
