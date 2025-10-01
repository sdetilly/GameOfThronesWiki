package com.tillylabs.gameofthroneswiki.ui.books

import com.tillylabs.gameofthroneswiki.models.BookWithCover
import com.tillylabs.gameofthroneswiki.usecase.BookDetailsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BookDetailsViewModelTest {
    private val mockBookDetailsUseCase = mockk<BookDetailsUseCase>()
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
    fun `should load book details successfully`() =
        runTest(testDispatcher) {
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

            coEvery { mockBookDetailsUseCase.getBookByUrl(bookUrl) } returns expectedBook

            // When
            val viewModel = BookDetailsViewModel(mockBookDetailsUseCase)
            viewModel.loadBookDetails(bookUrl)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.first { !it.isLoading }
            assertEquals(expectedBook, finalState.book)
            assertNull(finalState.error)
            assertFalse(finalState.isLoading)

            coVerify(exactly = 1) { mockBookDetailsUseCase.getBookByUrl(bookUrl) }
        }

    @Test
    fun `should handle book not found`() =
        runTest(testDispatcher) {
            // Given
            val bookUrl = "https://anapioficeandfire.com/api/books/999"

            coEvery { mockBookDetailsUseCase.getBookByUrl(bookUrl) } returns null

            // When
            val viewModel = BookDetailsViewModel(mockBookDetailsUseCase)
            viewModel.loadBookDetails(bookUrl)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.first { !it.isLoading }
            assertNull(finalState.book)
            assertEquals("Book not found", finalState.error)
            assertFalse(finalState.isLoading)

            coVerify(exactly = 1) { mockBookDetailsUseCase.getBookByUrl(bookUrl) }
        }

    @Test
    fun `should handle error state`() =
        runTest(testDispatcher) {
            // Given
            val bookUrl = "https://anapioficeandfire.com/api/books/1"
            val errorMessage = "Database error"

            coEvery { mockBookDetailsUseCase.getBookByUrl(bookUrl) } throws RuntimeException(errorMessage)

            // When
            val viewModel = BookDetailsViewModel(mockBookDetailsUseCase)
            viewModel.loadBookDetails(bookUrl)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.first { !it.isLoading }
            assertNull(finalState.book)
            assertEquals(errorMessage, finalState.error)
            assertFalse(finalState.isLoading)

            coVerify(exactly = 1) { mockBookDetailsUseCase.getBookByUrl(bookUrl) }
        }

    @Test
    fun `should show loading state initially and when loading new book`() =
        runTest(testDispatcher) {
            // Given
            val bookUrl = "https://anapioficeandfire.com/api/books/1"
            val book =
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

            coEvery { mockBookDetailsUseCase.getBookByUrl(bookUrl) } returns book

            // When
            val viewModel = BookDetailsViewModel(mockBookDetailsUseCase)

            // Then - initial state should be loading
            assertTrue(viewModel.uiState.value.isLoading)

            // When loading book details
            viewModel.loadBookDetails(bookUrl)

            // Then - should still be loading initially
            assertTrue(viewModel.uiState.value.isLoading)

            // Complete the coroutine
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - should not be loading anymore
            assertFalse(viewModel.uiState.value.isLoading)
            assertNotNull(viewModel.uiState.value.book)
        }
}
