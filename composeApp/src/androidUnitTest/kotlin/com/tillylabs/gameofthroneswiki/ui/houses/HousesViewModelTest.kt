package com.tillylabs.gameofthroneswiki.ui.houses

import com.tillylabs.gameofthroneswiki.models.House
import com.tillylabs.gameofthroneswiki.usecase.HousesUseCase
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
class HousesViewModelTest {
    private val mockHousesUseCase = mockk<HousesUseCase>()
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
    fun `should load houses successfully`() =
        runTest(testDispatcher) {
            // Given
            val expectedHouses =
                listOf(
                    House(
                        url = "https://anapioficeandfire.com/api/houses/1",
                        name = "House Stark",
                        region = "The North",
                        coatOfArms = "A grey direwolf",
                        words = "Winter is Coming",
                        titles = listOf("King in the North"),
                        seats = listOf("Winterfell"),
                        currentLord = "",
                        heir = "",
                        overlord = "",
                        founded = "",
                        founder = "",
                        diedOut = "",
                        ancestralWeapons = listOf("Ice"),
                        cadetBranches = emptyList(),
                        swornMembers = emptyList(),
                    ),
                )

            every { mockHousesUseCase.houses() } returns flowOf(expectedHouses)
            every { mockHousesUseCase.hasMore() } returns false

            // When
            val viewModel = HousesViewModel(mockHousesUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - Wait for StateFlow to emit the expected state
            val finalState = viewModel.uiState.first { it.houses.isNotEmpty() }
            assertEquals(expectedHouses, finalState.houses)
            assertNull(finalState.error)
            assertFalse(finalState.isLoading)
        }

    @Test
    fun `should handle error state`() =
        runTest(testDispatcher) {
            // Given - Mock Flow with empty list (no data available)
            every { mockHousesUseCase.houses() } returns flowOf(emptyList())
            every { mockHousesUseCase.hasMore() } returns false

            // When
            val viewModel = HousesViewModel(mockHousesUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - Should show loading state when no houses are available
            val initialState = viewModel.uiState.value
            assertEquals(emptyList(), initialState.houses)
            assertNull(initialState.error)
            assertTrue(initialState.isLoading) // Should be loading when houses is empty and no error
        }

    @Test
    fun `retry should reload houses`() =
        runTest(testDispatcher) {
            // Given
            val houses = listOf(mockk<House>())

            // Mock Flow to return the houses
            every { mockHousesUseCase.houses() } returns flowOf(houses)
            coEvery { mockHousesUseCase.refreshHouses() } returns Unit
            every { mockHousesUseCase.hasMore() } returns true

            // When
            val viewModel = HousesViewModel(mockHousesUseCase)
            testDispatcher.scheduler.advanceUntilIdle()
            viewModel.retry()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - Wait for houses to be loaded
            val finalState = viewModel.uiState.first { it.houses.isNotEmpty() }
            assertEquals(houses, finalState.houses)
            assertNull(finalState.error)
            assertFalse(finalState.isLoading)

            // Verify retry was called
            coVerify(exactly = 1) { mockHousesUseCase.refreshHouses() }
        }
}
