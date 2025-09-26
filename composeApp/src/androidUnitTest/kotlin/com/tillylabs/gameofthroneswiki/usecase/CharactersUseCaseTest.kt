package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import com.tillylabs.gameofthroneswiki.testutils.createCharacter
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CharactersUseCaseTest {
    private val mockRepository = mockk<GameOfThronesRepository>()
    private val useCase = CharactersUseCaseImpl(mockRepository)

    @Test
    fun `characters should return Flow from repository`() =
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
            every { mockRepository.getCharacters() } returns flowOf(expectedCharacters)

            // When
            val result = useCase.characters().first()

            // Then
            assertEquals(expectedCharacters, result)
        }

    @Test
    fun `refreshCharacters should call repository refresh`() =
        runTest {
            // Given
            coEvery { mockRepository.refreshCharacters() } returns Unit

            // When
            useCase.refreshCharacters()

            // Then
            coVerify(exactly = 1) { mockRepository.refreshCharacters() }
        }

    @Test
    fun `loadMore should filter out characters with empty names`() =
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
            coEvery { mockRepository.loadMoreCharacters() } returns charactersFromRepository

            // When
            val result = useCase.loadMore()

            // Then
            assertEquals(expectedCharacters, result)
            assertEquals(2, result.size)
            coVerify(exactly = 1) { mockRepository.loadMoreCharacters() }
        }

    @Test
    fun `hasMore should return repository value`() =
        runTest {
            // Given
            every { mockRepository.hasMoreCharacters() } returns true

            // When
            val result = useCase.hasMore()

            // Then
            assertEquals(true, result)
        }
}
