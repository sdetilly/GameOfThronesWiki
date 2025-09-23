package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import com.tillylabs.gameofthroneswiki.testutils.createCharacter
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetCharactersUseCaseTest {
    private val mockRepository = mockk<GameOfThronesRepository>()
    private val useCase = CharactersUseCase(mockRepository)

    @Test
    fun `invoke should return characters from repository`() =
        runTest {
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
                    createCharacter(
                        url = "https://anapioficeandfire.com/api/characters/2",
                        name = "Tyrion Lannister",
                        culture = "Westeros",
                        born = "In 273 AC",
                        titles = listOf("Hand of the King"),
                        aliases = listOf("The Imp"),
                    ),
                )
            coEvery { mockRepository.getCharacters() } returns expectedCharacters

            // When
            val result = useCase.characters()

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
                useCase.characters()
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
            val result = useCase.characters()

            // Then
            assertEquals(emptyList(), result)
            coVerify(exactly = 1) { mockRepository.getCharacters() }
        }

    @Test
    fun `invoke should filter out characters with empty names`() =
        runTest {
            // Given
            val charactersFromRepository =
                listOf(
                    createCharacter(
                        url = "https://anapioficeandfire.com/api/characters/1",
                        name = "Jon Snow",
                        culture = "Northmen",
                        titles = listOf("Lord Commander of the Night's Watch"),
                        aliases = listOf("Lord Snow"),
                    ),
                    createCharacter(
                        url = "https://anapioficeandfire.com/api/characters/2",
                        name = "",
                        culture = "Westeros",
                        born = "In 273 AC",
                    ),
                    createCharacter(
                        url = "https://anapioficeandfire.com/api/characters/3",
                        name = "Tyrion Lannister",
                        culture = "Westeros",
                        born = "In 273 AC",
                        titles = listOf("Hand of the King"),
                        aliases = listOf("The Imp"),
                    ),
                )
            val expectedCharacters = charactersFromRepository.filter { it.name.isNotEmpty() }
            coEvery { mockRepository.getCharacters() } returns charactersFromRepository

            // When
            val result = useCase.characters()

            // Then
            assertEquals(expectedCharacters, result)
            assertEquals(2, result.size)
            coVerify(exactly = 1) { mockRepository.getCharacters() }
        }
}
