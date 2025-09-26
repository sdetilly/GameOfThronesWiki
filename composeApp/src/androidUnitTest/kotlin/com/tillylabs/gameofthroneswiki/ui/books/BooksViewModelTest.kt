@file:Suppress("UnusedFlow")

package com.tillylabs.gameofthroneswiki.ui.books

import com.tillylabs.gameofthroneswiki.models.BookWithCover
import com.tillylabs.gameofthroneswiki.usecase.BooksUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class BooksViewModelTest {
    private val mockBooksUseCase = mockk<BooksUseCase>()
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should load books successfully`() =
        runTest(testDispatcher) {
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
                        coverImageUrl = "https://example.com/cover.jpg",
                    ),
                )
            coEvery { mockBooksUseCase.booksWithCover() } returns flowOf(expectedBooks)

            // When
            val viewModel = BooksViewModel(mockBooksUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse(finalState.isLoading)
            assertEquals(expectedBooks, finalState.books)
            assertNull(finalState.error)

            coVerify(exactly = 1) { mockBooksUseCase.booksWithCover() }
        }

    @Test
    fun `should handle error state`() =
        runTest(testDispatcher) {
            // Given
            val errorMessage = "Network error"
            coEvery { mockBooksUseCase.booksWithCover() } returns
                flow {
                    throw RuntimeException(errorMessage)
                }

            // When
            val viewModel = BooksViewModel(mockBooksUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val errorState = viewModel.uiState.value
            assertFalse(errorState.isLoading)
            assertEquals(emptyList(), errorState.books)
            assertEquals(errorMessage, errorState.error)

            coVerify(exactly = 1) { mockBooksUseCase.booksWithCover() }
        }

    @Test
    fun `retry should reload books`() =
        runTest(testDispatcher) {
            // Given
            val books = listOf(mockk<BookWithCover>())
            coEvery { mockBooksUseCase.booksWithCover() } returns flowOf(books)

            // When
            val viewModel = BooksViewModel(mockBooksUseCase)
            testDispatcher.scheduler.advanceUntilIdle()
            viewModel.retry()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse(finalState.isLoading)
            assertEquals(books, finalState.books)
            assertNull(finalState.error)

            coVerify(exactly = 2) { mockBooksUseCase.booksWithCover() }
        }

    @Test
    fun `should handle empty books list`() =
        runTest(testDispatcher) {
            // Given
            coEvery { mockBooksUseCase.booksWithCover() } returns flowOf(emptyList())

            // When
            val viewModel = BooksViewModel(mockBooksUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse(finalState.isLoading)
            assertEquals(emptyList(), finalState.books)
            assertNull(finalState.error)
            coVerify(exactly = 1) { mockBooksUseCase.booksWithCover() }
        }
}
