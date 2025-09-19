package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.models.Book
import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import org.koin.core.annotation.Factory

@Factory
class GetBooksUseCase(
    private val repository: GameOfThronesRepository,
) {
    suspend operator fun invoke(): List<Book> = repository.getBooks()
}
