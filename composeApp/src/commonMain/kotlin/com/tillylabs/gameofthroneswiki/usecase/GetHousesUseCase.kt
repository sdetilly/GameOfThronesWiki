package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.models.House
import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import org.koin.core.annotation.Factory

@Factory
class GetHousesUseCase(
    private val repository: GameOfThronesRepository,
) {
    suspend operator fun invoke(): List<House> = repository.getHouses()

    suspend fun loadMore(): List<House> = repository.loadMoreHouses()

    fun hasMore(): Boolean = repository.hasMoreHouses()
}
