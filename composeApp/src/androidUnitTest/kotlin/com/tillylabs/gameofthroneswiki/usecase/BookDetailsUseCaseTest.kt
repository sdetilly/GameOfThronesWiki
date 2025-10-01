package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.models.BookWithCover
import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BookDetailsUseCaseTest {
    private val mockRepository = mockk<GameOfThronesRepository>()
    private val useCase = BookDetailsUseCase(mockRepository)

    @Test
    fun `getBookByUrl should return book when found`() =
        runTest {
            // Given
            val bookUrl = "https://anapioficeandfire.com/api/books/1"
            val expectedBook =
                BookWithCover(
                    url = bookUrl,
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
                    coverImageUrl = "https://example.com/cover.jpg",
                )

            coEvery { mockRepository.getBookByUrl(bookUrl) } returns expectedBook

            // When
            val result = useCase.getBookByUrl(bookUrl)

            // Then
            assertEquals(expectedBook, result)
            coVerify(exactly = 1) { mockRepository.getBookByUrl(bookUrl) }
        }

    @Test
    fun `getBookByUrl should return null when book not found`() =
        runTest {
            // Given
            val bookUrl = "https://anapioficeandfire.com/api/books/999"

            coEvery { mockRepository.getBookByUrl(bookUrl) } returns null

            // When
            val result = useCase.getBookByUrl(bookUrl)

            // Then
            assertNull(result)
            coVerify(exactly = 1) { mockRepository.getBookByUrl(bookUrl) }
        }

    @Test
    fun `getBookByUrl should propagate repository exceptions`() =
        runTest {
            // Given
            val bookUrl = "https://anapioficeandfire.com/api/books/1"
            val exception = RuntimeException("Database error")

            coEvery { mockRepository.getBookByUrl(bookUrl) } throws exception

            // When & Then
            try {
                useCase.getBookByUrl(bookUrl)
                assert(false) { "Expected exception to be thrown" }
            } catch (e: RuntimeException) {
                assertEquals("Database error", e.message)
            }

            coVerify(exactly = 1) { mockRepository.getBookByUrl(bookUrl) }
        }
}
