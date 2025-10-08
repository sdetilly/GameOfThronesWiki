package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.models.CharacterWithBooks
import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import org.koin.core.annotation.Single

@Single
class CharacterDetailsUseCase(
    private val repository: GameOfThronesRepository,
) {
    suspend fun getCharacterWithBooks(url: String): CharacterWithBooks? {
        val character = repository.getCharacterByUrl(url) ?: return null

        val books = repository.getBooksByUrls(character.books)
        val povBooks = repository.getBooksByUrls(character.povBooks)

        return CharacterWithBooks(
            character = character,
            books = books,
            povBooks = povBooks,
        )
    }
}
