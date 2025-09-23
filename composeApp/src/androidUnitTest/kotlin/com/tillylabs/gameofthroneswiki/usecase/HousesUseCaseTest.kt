package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.models.House
import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HousesUseCaseTest {
    private val mockRepository = mockk<GameOfThronesRepository>()
    private val useCase = HousesUseCaseImpl(mockRepository)

    @Test
    fun `invoke should return houses from repository`() =
        runTest {
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
                    House(
                        url = "https://anapioficeandfire.com/api/houses/2",
                        name = "House Lannister",
                        region = "The Westerlands",
                        coatOfArms = "A golden lion",
                        words = "Hear Me Roar!",
                        titles = listOf("Lord of Casterly Rock"),
                        seats = listOf("Casterly Rock"),
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
            coEvery { mockRepository.getHouses() } returns expectedHouses

            // When
            val result = useCase.houses()

            // Then
            assertEquals(expectedHouses, result)
            coVerify(exactly = 1) { mockRepository.getHouses() }
        }

    @Test
    fun `invoke should propagate repository exceptions`() =
        runTest {
            // Given
            val expectedException = RuntimeException("Repository error")
            coEvery { mockRepository.getHouses() } throws expectedException

            // When & Then
            try {
                useCase.houses()
                throw AssertionError("Should have thrown exception")
            } catch (e: Exception) {
                assertEquals(expectedException, e)
            }

            coVerify(exactly = 1) { mockRepository.getHouses() }
        }

    @Test
    fun `invoke should return empty list when repository returns empty list`() =
        runTest {
            // Given
            coEvery { mockRepository.getHouses() } returns emptyList()

            // When
            val result = useCase.houses()

            // Then
            assertEquals(emptyList(), result)
            coVerify(exactly = 1) { mockRepository.getHouses() }
        }
}
