package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.models.Book
import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetBooksUseCaseTest {
    private val mockRepository = mockk<GameOfThronesRepository>()
    private val useCase = GetBooksUseCase(mockRepository)

    @Test
    fun `invoke should return books from repository`() =
        runTest {
            // Given
            val expectedBooks =
                listOf(
                    Book(
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
                    ),
                    Book(
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
                    ),
                )
            coEvery { mockRepository.getBooks() } returns expectedBooks

            // When
            val result = useCase()

            // Then
            assertEquals(expectedBooks, result)
            coVerify(exactly = 1) { mockRepository.getBooks() }
        }

    @Test
    fun `invoke should propagate repository exceptions`() =
        runTest {
            // Given
            val expectedException = RuntimeException("Repository error")
            coEvery { mockRepository.getBooks() } throws expectedException

            // When & Then
            try {
                useCase()
                throw AssertionError("Should have thrown exception")
            } catch (e: Exception) {
                assertEquals(expectedException, e)
            }

            coVerify(exactly = 1) { mockRepository.getBooks() }
        }

    @Test
    fun `invoke should return empty list when repository returns empty list`() =
        runTest {
            // Given
            coEvery { mockRepository.getBooks() } returns emptyList()

            // When
            val result = useCase()

            // Then
            assertEquals(emptyList(), result)
            coVerify(exactly = 1) { mockRepository.getBooks() }
        }
}
