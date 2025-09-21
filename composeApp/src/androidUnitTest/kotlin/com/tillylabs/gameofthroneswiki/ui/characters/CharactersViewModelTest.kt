package com.tillylabs.gameofthroneswiki.ui.characters

import com.tillylabs.gameofthroneswiki.testutils.createCharacter
import com.tillylabs.gameofthroneswiki.usecase.GetCharactersUseCase
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
    private val mockGetCharactersUseCase = mockk<GetCharactersUseCase>()
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
            coEvery { mockGetCharactersUseCase() } returns expectedCharacters
            coEvery { mockGetCharactersUseCase.hasMore() } returns false

            // When
            val viewModel = CharactersViewModel(mockGetCharactersUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse(finalState.isLoading)
            assertEquals(expectedCharacters, finalState.characters)
            assertNull(finalState.error)
            assertFalse(finalState.hasMoreData)

            coVerify(exactly = 1) { mockGetCharactersUseCase() }
            coVerify(exactly = 1) { mockGetCharactersUseCase.hasMore() }
        }

    @Test
    fun `should handle error state`() =
        runTest(testDispatcher) {
            // Given
            val errorMessage = "API error"
            coEvery { mockGetCharactersUseCase() } throws RuntimeException(errorMessage)

            // When
            val viewModel = CharactersViewModel(mockGetCharactersUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val errorState = viewModel.uiState.value
            assertFalse(errorState.isLoading)
            assertEquals(emptyList(), errorState.characters)
            assertEquals(errorMessage, errorState.error)

            coVerify(exactly = 1) { mockGetCharactersUseCase() }
        }

    @Test
    fun `retry should reload characters`() =
        runTest(testDispatcher) {
            // Given
            val characters = listOf(createCharacter(name = "Test Character"))
            coEvery { mockGetCharactersUseCase() } returns characters
            coEvery { mockGetCharactersUseCase.hasMore() } returns true

            // When
            val viewModel = CharactersViewModel(mockGetCharactersUseCase)
            testDispatcher.scheduler.advanceUntilIdle()
            viewModel.retry()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse(finalState.isLoading)
            assertEquals(characters, finalState.characters)
            assertNull(finalState.error)

            coVerify(exactly = 2) { mockGetCharactersUseCase() }
            coVerify(exactly = 2) { mockGetCharactersUseCase.hasMore() }
        }
}
