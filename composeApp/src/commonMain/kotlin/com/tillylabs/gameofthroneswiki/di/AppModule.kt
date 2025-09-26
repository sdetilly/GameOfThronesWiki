package com.tillylabs.gameofthroneswiki.di

import com.tillylabs.gameofthroneswiki.database.GameOfThronesDatabase
import com.tillylabs.gameofthroneswiki.database.getRoomDatabase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.tillylabs.gameofthroneswiki")
class AppModule {
    @Single
    fun provideDatabase(builder: androidx.room.RoomDatabase.Builder<GameOfThronesDatabase>): GameOfThronesDatabase =
        getRoomDatabase(builder)
}
