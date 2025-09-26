package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.models.BookWithCover
import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BooksUseCaseTest {
    private val mockRepository = mockk<GameOfThronesRepository>()
    private val useCase = BooksUseCaseImpl(mockRepository)

    @Test
    fun `invoke should return books from repository`() =
        runTest {
            // Given
            val expectedBooks =
                listOf(
                    BookWithCover(
                        url = "https://anapioficeandfire.com/api/books/1",
                        name = "A Game of Thrones",
                        isbn = "978-0553103540",
                        authors = listOf("George R. R. Martin"),
                        numberOfPages = 694,
                        publisher = "Bantam Books",
                        country = "United States",
                        mediaType = "Hardcover",
                        released = "1996-08-01T00:00:00",
                        characters = emptyList(),
                        povCharacters = emptyList(),
                        coverImageUrl = "https://example.com/got-cover.jpg",
                    ),
                    BookWithCover(
                        url = "https://anapioficeandfire.com/api/books/2",
                        name = "A Clash of Kings",
                        isbn = "978-0553108033",
                        authors = listOf("George R. R. Martin"),
                        numberOfPages = 761,
                        publisher = "Bantam Books",
                        country = "United States",
                        mediaType = "Hardcover",
                        released = "1999-02-02T00:00:00",
                        characters = emptyList(),
                        povCharacters = emptyList(),
                        coverImageUrl = "https://example.com/cok-cover.jpg",
                    ),
                )
            coEvery { mockRepository.getBooks() } returns flowOf(expectedBooks)

            // When
            val result = useCase.booksWithCover()

            // Then
            assertEquals(expectedBooks, result.first())
        }

    @Test
    fun `invoke should propagate repository exceptions`() =
        runTest {
            // Given
            val expectedException = RuntimeException("Repository error")
            coEvery { mockRepository.getBooks() } throws expectedException

            // When & Then
            try {
                useCase.booksWithCover().first()
                throw AssertionError("Should have thrown exception")
            } catch (e: Exception) {
                assertEquals(expectedException, e)
            }
        }

    @Test
    fun `invoke should return empty list when repository returns empty list`() =
        runTest {
            // Given
            coEvery { mockRepository.getBooks() } returns flowOf(emptyList())

            // When
            val result = useCase.booksWithCover()

            // Then
            assertEquals(emptyList(), result.first())
        }
}
