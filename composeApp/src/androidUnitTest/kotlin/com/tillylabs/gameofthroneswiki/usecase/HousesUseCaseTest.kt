package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HousesUseCaseTest {
    private val mockRepository = mockk<GameOfThronesRepository>()
    private val useCase = HousesUseCaseImpl(mockRepository)

    @Test
    fun `refreshHouses should trigger refreshHouses from repository`() =
        runTest {
            // Given
            coEvery { mockRepository.refreshHouses() } returns Unit

            // When
            useCase.refreshHouses()

            // Then
            coVerify(exactly = 1) { mockRepository.refreshHouses() }
        }

    @Test
    fun `invoke should propagate repository exceptions`() =
        runTest {
            // Given
            val expectedException = RuntimeException("Repository error")
            coEvery { mockRepository.refreshHouses() } throws expectedException

            // When & Then
            try {
                useCase.refreshHouses()
                throw AssertionError("Should have thrown exception")
            } catch (e: Exception) {
                assertEquals(expectedException, e)
            }
        }

    @Test
    fun `houses should return Flow from repository`() =
        runTest {
            // Given
            val expectedHouses =
                listOf(
                    com.tillylabs.gameofthroneswiki.models.House(
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
            every { mockRepository.getHouses() } returns flowOf(expectedHouses)

            // When
            val result = useCase.houses()

            // Then
            assertEquals(expectedHouses, result.first())
        }

    @Test
    fun `loadMore should filter out houses with empty names`() =
        runTest {
            // Given
            val housesFromRepository =
                listOf(
                    com.tillylabs.gameofthroneswiki.models.House(
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
                    com.tillylabs.gameofthroneswiki.models.House(
                        url = "https://anapioficeandfire.com/api/houses/2",
                        name = "",
                        region = "The North",
                        coatOfArms = "",
                        words = "",
                        titles = emptyList(),
                        seats = emptyList(),
                        currentLord = "",
                        heir = "",
                        overlord = "",
                        founded = "",
                        founder = "",
                        diedOut = "",
                        ancestralWeapons = emptyList(),
                        cadetBranches = emptyList(),
                        swornMembers = emptyList(),
                    ),
                )
            val expectedHouses = housesFromRepository.filter { it.name.isNotEmpty() }
            coEvery { mockRepository.loadMoreHouses() } returns housesFromRepository

            // When
            val result = useCase.loadMore()

            // Then
            assertEquals(expectedHouses, result)
            assertEquals(1, result.size)
            coVerify(exactly = 1) { mockRepository.loadMoreHouses() }
        }

    @Test
    fun `hasMore should return repository value`() =
        runTest {
            // Given
            every { mockRepository.hasMoreHouses() } returns true

            // When
            val result = useCase.hasMore()

            // Then
            assertEquals(true, result)
        }
}
