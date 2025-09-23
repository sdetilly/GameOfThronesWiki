package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.models.Character
import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import org.koin.core.annotation.Factory

@Factory
class CharactersUseCase(
    private val repository: GameOfThronesRepository,
) {
    suspend fun characters(): List<Character> =
        repository
            .getCharacters()
            .filter { it.name.isNotEmpty() }

    suspend fun loadMore(): List<Character> =
        repository
            .loadMoreCharacters()
            .filter { it.name.isNotEmpty() }

    fun hasMore(): Boolean = repository.hasMoreCharacters()
}
