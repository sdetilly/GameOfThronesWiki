package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.models.BookWithCover
import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import org.koin.core.annotation.Factory

@Factory
class BooksUseCase(
    private val repository: GameOfThronesRepository,
) {
    suspend fun booksWithCover(): List<BookWithCover> = repository.getBooks()
}
