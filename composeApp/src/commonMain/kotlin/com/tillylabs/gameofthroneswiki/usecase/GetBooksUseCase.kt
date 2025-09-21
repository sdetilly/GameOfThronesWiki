package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.models.BookWithCover
import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import org.koin.core.annotation.Factory

@Factory
class GetBooksUseCase(
    private val repository: GameOfThronesRepository,
) {
    suspend operator fun invoke(): List<BookWithCover> = repository.getBooks()

    suspend fun loadMore(): List<BookWithCover> = repository.loadMoreBooks()

    fun hasMore(): Boolean = repository.hasMoreBooks()
}
