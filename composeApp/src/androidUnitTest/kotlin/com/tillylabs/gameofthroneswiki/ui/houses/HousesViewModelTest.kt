package com.tillylabs.gameofthroneswiki.ui.houses

import com.tillylabs.gameofthroneswiki.models.House
import com.tillylabs.gameofthroneswiki.usecase.HousesUseCase
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
            coEvery { mockHousesUseCase.houses() } returns expectedHouses
            coEvery { mockHousesUseCase.hasMore() } returns false

            // When
            val viewModel = HousesViewModel(mockHousesUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse(finalState.isLoading)
            assertEquals(expectedHouses, finalState.houses)
            assertNull(finalState.error)
            assertFalse(finalState.hasMoreData)

            coVerify(exactly = 1) { mockHousesUseCase.houses() }
            coVerify(exactly = 1) { mockHousesUseCase.hasMore() }
        }

    @Test
    fun `should handle error state`() =
        runTest(testDispatcher) {
            // Given
            val errorMessage = "Server error"
            coEvery { mockHousesUseCase.houses() } throws RuntimeException(errorMessage)

            // When
            val viewModel = HousesViewModel(mockHousesUseCase)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val errorState = viewModel.uiState.value
            assertFalse(errorState.isLoading)
            assertEquals(emptyList(), errorState.houses)
            assertEquals(errorMessage, errorState.error)

            coVerify(exactly = 1) { mockHousesUseCase.houses() }
        }

    @Test
    fun `retry should reload houses`() =
        runTest(testDispatcher) {
            // Given
            val houses = listOf(mockk<House>())
            coEvery { mockHousesUseCase.houses() } returns houses
            coEvery { mockHousesUseCase.hasMore() } returns true

            // When
            val viewModel = HousesViewModel(mockHousesUseCase)
            testDispatcher.scheduler.advanceUntilIdle()
            viewModel.retry()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse(finalState.isLoading)
            assertEquals(houses, finalState.houses)
            assertNull(finalState.error)

            coVerify(exactly = 2) { mockHousesUseCase.houses() }
            coVerify(exactly = 2) { mockHousesUseCase.hasMore() }
        }
}
