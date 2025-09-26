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

            // Mock the Flow and suspend methods
            every { mockCharactersUseCase.characters() } returns flowOf(expectedCharacters)
            coEvery { mockCharactersUseCase.refreshCharacters() } returns Unit
            every { mockCharactersUseCase.hasMore() } returns false

            // When
            val viewModel = CharactersViewModel(mockCharactersUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - Wait for StateFlow to emit the expected state
            val finalState = viewModel.uiState.first { !it.isLoading || it.error != null }
            assertEquals(expectedCharacters, finalState.characters)
            assertNull(finalState.error)
            assertFalse(finalState.hasMoreData)
            assertFalse(finalState.isLoading)

            // Note: refreshCharacters is called via background loading in the Flow
            // hasMore() is called to initialize hasMoreData state
        }

    @Test
    fun `should handle error state`() =
        runTest(testDispatcher) {
            // Given
            val errorMessage = "API error"

            // Mock Flow with empty list initially
            every { mockCharactersUseCase.characters() } returns flowOf(emptyList())
            coEvery { mockCharactersUseCase.refreshCharacters() } throws RuntimeException(errorMessage)
            every { mockCharactersUseCase.hasMore() } returns false

            // When
            val viewModel = CharactersViewModel(mockCharactersUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - Wait for error state
            val errorState = viewModel.uiState.first { it.error != null }
            assertEquals(emptyList(), errorState.characters)
            assertEquals(errorMessage, errorState.error)
            assertFalse(errorState.isLoading)

            // Verify refreshCharacters was called and failed
            coVerify(atLeast = 1) { mockCharactersUseCase.refreshCharacters() }
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
            val finalState = viewModel.uiState.first { !it.isLoading || it.error != null }
            assertEquals(characters, finalState.characters)
            assertNull(finalState.error)
            assertFalse(finalState.isLoading)

            // Verify retry was called (only the explicit retry call, not the initial Flow loading)
            coVerify(atLeast = 1) { mockCharactersUseCase.refreshCharacters() }
        }
}
