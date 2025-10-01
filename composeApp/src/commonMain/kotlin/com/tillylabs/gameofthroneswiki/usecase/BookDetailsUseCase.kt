package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.models.BookWithCover
import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import org.koin.core.annotation.Single

@Single
class BookDetailsUseCase(
    private val repository: GameOfThronesRepository,
) {
    suspend fun getBookByUrl(url: String): BookWithCover? = repository.getBookByUrl(url)
}
