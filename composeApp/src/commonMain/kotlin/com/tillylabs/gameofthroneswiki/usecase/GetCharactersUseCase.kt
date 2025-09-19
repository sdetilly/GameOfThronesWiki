package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.models.Character
import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import org.koin.core.annotation.Factory

@Factory
class GetCharactersUseCase(
    private val repository: GameOfThronesRepository,
) {
    suspend operator fun invoke(): List<Character> = repository.getCharacters()
}
