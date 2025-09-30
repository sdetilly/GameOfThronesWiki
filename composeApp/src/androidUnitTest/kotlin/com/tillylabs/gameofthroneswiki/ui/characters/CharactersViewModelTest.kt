package com.tillylabs.gameofthroneswiki.ui.characters

import com.tillylabs.gameofthroneswiki.testutils.createCharacter
import com.tillylabs.gameofthroneswiki.usecase.CharactersUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CharactersViewModelTest {
    private val mockCharactersUseCase = mockk<CharactersUseCase>()
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
    fun `should load characters successfully`() =
        runTest(testDispatcher) {
            // Given
            val expectedCharacters =
                listOf(
                    createCharacter(
                        url = "https://anapioficeandfire.com/api/characters/1",
                        name = "Jon Snow",
                        culture = "Northmen",
                        titles = listOf("Lord Commander of the Night's Watch"),
                        aliases = listOf("Lord Snow"),
                    ),
                )

            // Mock the Flow to return characters
            every { mockCharactersUseCase.characters() } returns flowOf(expectedCharacters)
            every { mockCharactersUseCase.hasMore() } returns false

            // When
            val viewModel = CharactersViewModel(mockCharactersUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - Wait for StateFlow to emit the expected state
            val finalState = viewModel.uiState.first { it.characters.isNotEmpty() }
            assertEquals(expectedCharacters, finalState.characters)
            assertNull(finalState.error)
            assertFalse(finalState.isLoading)
        }

    @Test
    fun `should handle error state`() =
        runTest(testDispatcher) {
            // Given - Mock Flow with empty list (no data available)
            every { mockCharactersUseCase.characters() } returns flowOf(emptyList())
            every { mockCharactersUseCase.hasMore() } returns false

            // When
            val viewModel = CharactersViewModel(mockCharactersUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - Should show loading state when no characters are available
            val initialState = viewModel.uiState.value
            assertEquals(emptyList(), initialState.characters)
            assertNull(initialState.error)
            assertTrue(initialState.isLoading) // Should be loading when characters is empty and no error
        }

    @Test
    fun `retry should reload characters`() =
        runTest(testDispatcher) {
            // Given
            val characters = listOf(createCharacter(name = "Test Character"))

            // Mock Flow to return the characters
            every { mockCharactersUseCase.characters() } returns flowOf(characters)
            coEvery { mockCharactersUseCase.refreshCharacters() } returns Unit
            every { mockCharactersUseCase.hasMore() } returns true

            // When
            val viewModel = CharactersViewModel(mockCharactersUseCase)
            testDispatcher.scheduler.advanceUntilIdle()
            viewModel.retry()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - Wait for characters to be loaded
            val finalState = viewModel.uiState.first { it.characters.isNotEmpty() }
            assertEquals(characters, finalState.characters)
            assertNull(finalState.error)
            assertFalse(finalState.isLoading)

            // Verify retry was called
            coVerify(exactly = 1) { mockCharactersUseCase.refreshCharacters() }
        }
}
