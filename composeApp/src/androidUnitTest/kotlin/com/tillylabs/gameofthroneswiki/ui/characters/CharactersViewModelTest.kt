package com.tillylabs.gameofthroneswiki.ui.characters

import com.tillylabs.gameofthroneswiki.testutils.createCharacter
import com.tillylabs.gameofthroneswiki.usecase.CharactersUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
            coEvery { mockCharactersUseCase.characters() } returns expectedCharacters
            coEvery { mockCharactersUseCase.hasMore() } returns false

            // When
            val viewModel = CharactersViewModel(mockCharactersUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse(finalState.isLoading)
            assertEquals(expectedCharacters, finalState.characters)
            assertNull(finalState.error)
            assertFalse(finalState.hasMoreData)

            coVerify(exactly = 1) { mockCharactersUseCase.characters() }
            coVerify(exactly = 1) { mockCharactersUseCase.hasMore() }
        }

    @Test
    fun `should handle error state`() =
        runTest(testDispatcher) {
            // Given
            val errorMessage = "API error"
            coEvery { mockCharactersUseCase.characters() } throws RuntimeException(errorMessage)

            // When
            val viewModel = CharactersViewModel(mockCharactersUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val errorState = viewModel.uiState.value
            assertFalse(errorState.isLoading)
            assertEquals(emptyList(), errorState.characters)
            assertEquals(errorMessage, errorState.error)

            coVerify(exactly = 1) { mockCharactersUseCase.characters() }
        }

    @Test
    fun `retry should reload characters`() =
        runTest(testDispatcher) {
            // Given
            val characters = listOf(createCharacter(name = "Test Character"))
            coEvery { mockCharactersUseCase.characters() } returns characters
            coEvery { mockCharactersUseCase.hasMore() } returns true

            // When
            val viewModel = CharactersViewModel(mockCharactersUseCase)
            testDispatcher.scheduler.advanceUntilIdle()
            viewModel.retry()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse(finalState.isLoading)
            assertEquals(characters, finalState.characters)
            assertNull(finalState.error)

            coVerify(exactly = 2) { mockCharactersUseCase.characters() }
            coVerify(exactly = 2) { mockCharactersUseCase.hasMore() }
        }
}
