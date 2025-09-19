package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.models.Character
import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetCharactersUseCaseTest {
    private val mockRepository = mockk<GameOfThronesRepository>()
    private val useCase = GetCharactersUseCase(mockRepository)

    @Test
    fun `invoke should return characters from repository`() =
        runTest {
            // Given
            val expectedCharacters =
                listOf(
                    Character(
                        url = "https://anapioficeandfire.com/api/characters/1",
                        name = "Jon Snow",
                        gender = "Male",
                        culture = "Northmen",
                        born = "In 283 AC",
                        died = "",
                        titles = listOf("Lord Commander of the Night's Watch"),
                        aliases = listOf("Lord Snow"),
                        father = "",
                        mother = "",
                        spouse = "",
                        allegiances = emptyList(),
                        books = emptyList(),
                        povBooks = emptyList(),
                        tvSeries = emptyList(),
                        playedBy = emptyList(),
                    ),
                    Character(
                        url = "https://anapioficeandfire.com/api/characters/2",
                        name = "Tyrion Lannister",
                        gender = "Male",
                        culture = "Westeros",
                        born = "In 273 AC",
                        died = "",
                        titles = listOf("Hand of the King"),
                        aliases = listOf("The Imp"),
                        father = "",
                        mother = "",
                        spouse = "",
                        allegiances = emptyList(),
                        books = emptyList(),
                        povBooks = emptyList(),
                        tvSeries = emptyList(),
                        playedBy = emptyList(),
                    ),
                )
            coEvery { mockRepository.getCharacters() } returns expectedCharacters

            // When
            val result = useCase()

            // Then
            assertEquals(expectedCharacters, result)
            coVerify(exactly = 1) { mockRepository.getCharacters() }
        }

    @Test
    fun `invoke should propagate repository exceptions`() =
        runTest {
            // Given
            val expectedException = RuntimeException("Repository error")
            coEvery { mockRepository.getCharacters() } throws expectedException

            // When & Then
            try {
                useCase()
                throw AssertionError("Should have thrown exception")
            } catch (e: Exception) {
                assertEquals(expectedException, e)
            }

            coVerify(exactly = 1) { mockRepository.getCharacters() }
        }

    @Test
    fun `invoke should return empty list when repository returns empty list`() =
        runTest {
            // Given
            coEvery { mockRepository.getCharacters() } returns emptyList()

            // When
            val result = useCase()

            // Then
            assertEquals(emptyList(), result)
            coVerify(exactly = 1) { mockRepository.getCharacters() }
        }
}
