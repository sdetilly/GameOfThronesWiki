package com.tillylabs.gameofthroneswiki.ui.characters

import com.tillylabs.gameofthroneswiki.models.BookWithCover
import com.tillylabs.gameofthroneswiki.models.CharacterWithBooks
import com.tillylabs.gameofthroneswiki.testutils.createCharacter
import com.tillylabs.gameofthroneswiki.usecase.CharacterDetailsUseCase
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
class CharacterDetailsViewModelTest {
    private val mockCharacterDetailsUseCase = mockk<CharacterDetailsUseCase>()
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
    fun `should start with loading state`() {
        // When
        val viewModel = CharacterDetailsViewModel(mockCharacterDetailsUseCase)

        // Then
        val initialState = viewModel.uiState.value
        assertTrue(initialState.isLoading)
        assertNull(initialState.characterWithBooks)
        assertNull(initialState.error)
    }

    @Test
    fun `should load character with books successfully`() =
        runTest(testDispatcher) {
            // Given
            val characterUrl = "https://anapioficeandfire.com/api/characters/583"
            val character = createCharacter(url = characterUrl, name = "Jon Snow")
            val books =
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
            val characterWithBooks = CharacterWithBooks(character, books, emptyList())

            coEvery { mockCharacterDetailsUseCase.getCharacterWithBooks(characterUrl) } returns characterWithBooks

            // When
            val viewModel = CharacterDetailsViewModel(mockCharacterDetailsUseCase)
            viewModel.loadCharacterDetails(characterUrl)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.first { !it.isLoading }
            assertFalse(finalState.isLoading)
            assertNotNull(finalState.characterWithBooks)
            assertEquals("Jon Snow", finalState.characterWithBooks?.character?.name)
            assertEquals(1, finalState.characterWithBooks?.books?.size)
            assertEquals(
                "A Game of Thrones",
                finalState.characterWithBooks
                    ?.books
                    ?.get(0)
                    ?.name,
            )
            assertNull(finalState.error)

            coVerify(exactly = 1) { mockCharacterDetailsUseCase.getCharacterWithBooks(characterUrl) }
        }

    @Test
    fun `should handle character not found`() =
        runTest(testDispatcher) {
            // Given
            val characterUrl = "https://anapioficeandfire.com/api/characters/999999"

            coEvery { mockCharacterDetailsUseCase.getCharacterWithBooks(characterUrl) } returns null

            // When
            val viewModel = CharacterDetailsViewModel(mockCharacterDetailsUseCase)
            viewModel.loadCharacterDetails(characterUrl)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.first { !it.isLoading }
            assertFalse(finalState.isLoading)
            assertNull(finalState.characterWithBooks)
            assertEquals("Character not found", finalState.error)

            coVerify(exactly = 1) { mockCharacterDetailsUseCase.getCharacterWithBooks(characterUrl) }
        }

    @Test
    fun `should handle use case exceptions`() =
        runTest(testDispatcher) {
            // Given
            val characterUrl = "https://anapioficeandfire.com/api/characters/1"
            val exception = RuntimeException("Network error")

            coEvery { mockCharacterDetailsUseCase.getCharacterWithBooks(characterUrl) } throws exception

            // When
            val viewModel = CharacterDetailsViewModel(mockCharacterDetailsUseCase)
            viewModel.loadCharacterDetails(characterUrl)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.first { !it.isLoading }
            assertFalse(finalState.isLoading)
            assertNull(finalState.characterWithBooks)
            assertEquals("Network error", finalState.error)

            coVerify(exactly = 1) { mockCharacterDetailsUseCase.getCharacterWithBooks(characterUrl) }
        }

    @Test
    fun `should handle character with POV books`() =
        runTest(testDispatcher) {
            // Given
            val characterUrl = "https://anapioficeandfire.com/api/characters/583"
            val character = createCharacter(url = characterUrl, name = "Jon Snow")
            val povBooks =
                listOf(
                    BookWithCover(
                        url = "https://anapioficeandfire.com/api/books/5",
                        name = "A Dance with Dragons",
                        isbn = "978-0553801477",
                        authors = listOf("George R. R. Martin"),
                        numberOfPages = 1016,
                        publisher = "Bantam Books",
                        country = "United States",
                        mediaType = "Hardcover",
                        released = "2011-07-12T00:00:00",
                        characters = emptyList(),
                        povCharacters = emptyList(),
                        coverImageUrl = "https://example.com/cover5.jpg",
                    ),
                )
            val characterWithBooks = CharacterWithBooks(character, emptyList(), povBooks)

            coEvery { mockCharacterDetailsUseCase.getCharacterWithBooks(characterUrl) } returns characterWithBooks

            // When
            val viewModel = CharacterDetailsViewModel(mockCharacterDetailsUseCase)
            viewModel.loadCharacterDetails(characterUrl)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.first { !it.isLoading }
            assertFalse(finalState.isLoading)
            assertNotNull(finalState.characterWithBooks)
            assertEquals(0, finalState.characterWithBooks?.books?.size)
            assertEquals(1, finalState.characterWithBooks?.povBooks?.size)
            assertEquals(
                "A Dance with Dragons",
                finalState.characterWithBooks
                    ?.povBooks
                    ?.get(0)
                    ?.name,
            )
            assertNull(finalState.error)
        }

    @Test
    fun `should clear previous error when loading new character`() =
        runTest(testDispatcher) {
            // Given
            val characterUrl1 = "https://anapioficeandfire.com/api/characters/1"
            val characterUrl2 = "https://anapioficeandfire.com/api/characters/2"
            val character = createCharacter(url = characterUrl2, name = "Arya Stark")
            val characterWithBooks = CharacterWithBooks(character, emptyList(), emptyList())

            coEvery { mockCharacterDetailsUseCase.getCharacterWithBooks(characterUrl1) } throws RuntimeException("Error")
            coEvery { mockCharacterDetailsUseCase.getCharacterWithBooks(characterUrl2) } returns characterWithBooks

            // When - first load with error
            val viewModel = CharacterDetailsViewModel(mockCharacterDetailsUseCase)
            viewModel.loadCharacterDetails(characterUrl1)
            testDispatcher.scheduler.advanceUntilIdle()

            val errorState = viewModel.uiState.first { !it.isLoading }
            assertEquals("Error", errorState.error)

            // When - second load successful
            viewModel.loadCharacterDetails(characterUrl2)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val successState = viewModel.uiState.first { it.characterWithBooks != null }
            assertFalse(successState.isLoading)
            assertNotNull(successState.characterWithBooks)
            assertEquals("Arya Stark", successState.characterWithBooks?.character?.name)
            assertNull(successState.error)
        }

    @Test
    fun `should handle multiple calls to loadCharacterDetails`() =
        runTest(testDispatcher) {
            // Given
            val characterUrl = "https://anapioficeandfire.com/api/characters/583"
            val character = createCharacter(url = characterUrl, name = "Jon Snow")
            val characterWithBooks = CharacterWithBooks(character, emptyList(), emptyList())

            coEvery { mockCharacterDetailsUseCase.getCharacterWithBooks(characterUrl) } returns characterWithBooks

            // When
            val viewModel = CharacterDetailsViewModel(mockCharacterDetailsUseCase)
            viewModel.loadCharacterDetails(characterUrl)
            viewModel.loadCharacterDetails(characterUrl)
            viewModel.loadCharacterDetails(characterUrl)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - should have called use case 3 times
            coVerify(exactly = 3) { mockCharacterDetailsUseCase.getCharacterWithBooks(characterUrl) }
        }
}
