package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.models.BookWithCover
import com.tillylabs.gameofthroneswiki.models.Character
import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CharacterDetailsUseCaseTest {
    private val mockRepository = mockk<GameOfThronesRepository>()
    private val useCase = CharacterDetailsUseCase(mockRepository)

    @Test
    fun `getCharacterWithBooks should return character with books when found`() =
        runTest {
            // Given
            val characterUrl = "https://anapioficeandfire.com/api/characters/583"
            val character =
                Character(
                    url = characterUrl,
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
                    allegiances = listOf("House Stark"),
                    books = listOf("https://anapioficeandfire.com/api/books/1"),
                    povBooks = listOf("https://anapioficeandfire.com/api/books/5"),
                    tvSeries = listOf("Season 1"),
                    playedBy = listOf("Kit Harington"),
                )

            val book =
                BookWithCover(
                    url = "https://anapioficeandfire.com/api/books/1",
                    name = "A Game of Thrones",
                    isbn = "978-0553103540",
                    authors = listOf("George R. R. Martin"),
                    numberOfPages = 694,
                    publisher = "Bantam Books",
                    country = "United States",
                    mediaType = "Hardcover",
                    released = "1996-08-01T00:00:00",
                    characters = emptyList(),
                    povCharacters = emptyList(),
                    coverImageUrl = "https://example.com/cover1.jpg",
                )

            val povBook =
                BookWithCover(
                    url = "https://anapioficeandfire.com/api/books/5",
                    name = "A Dance with Dragons",
                    isbn = "978-0553801477",
                    authors = listOf("George R. R. Martin"),
                    numberOfPages = 1016,
                    publisher = "Bantam Books",
                    country = "United States",
                    mediaType = "Hardcover",
                    released = "2011-07-12T00:00:00",
                    characters = emptyList(),
                    povCharacters = emptyList(),
                    coverImageUrl = "https://example.com/cover5.jpg",
                )

            coEvery { mockRepository.getCharacterByUrl(characterUrl) } returns character
            coEvery { mockRepository.getBooksByUrls(character.books) } returns listOf(book)
            coEvery { mockRepository.getBooksByUrls(character.povBooks) } returns listOf(povBook)

            // When
            val result = useCase.getCharacterWithBooks(characterUrl)

            // Then
            assertEquals(character, result?.character)
            assertEquals(1, result?.books?.size)
            assertEquals("A Game of Thrones", result?.books?.get(0)?.name)
            assertEquals(1, result?.povBooks?.size)
            assertEquals("A Dance with Dragons", result?.povBooks?.get(0)?.name)

            coVerify(exactly = 1) { mockRepository.getCharacterByUrl(characterUrl) }
            coVerify(exactly = 1) { mockRepository.getBooksByUrls(character.books) }
            coVerify(exactly = 1) { mockRepository.getBooksByUrls(character.povBooks) }
        }

    @Test
    fun `getCharacterWithBooks should return null when character not found`() =
        runTest {
            // Given
            val characterUrl = "https://anapioficeandfire.com/api/characters/999999"

            coEvery { mockRepository.getCharacterByUrl(characterUrl) } returns null

            // When
            val result = useCase.getCharacterWithBooks(characterUrl)

            // Then
            assertNull(result)
            coVerify(exactly = 1) { mockRepository.getCharacterByUrl(characterUrl) }
            coVerify(exactly = 0) { mockRepository.getBooksByUrls(any()) }
        }

    @Test
    fun `getCharacterWithBooks should handle character with no books`() =
        runTest {
            // Given
            val characterUrl = "https://anapioficeandfire.com/api/characters/1"
            val character =
                Character(
                    url = characterUrl,
                    name = "Character Without Books",
                    gender = "Male",
                    culture = "Unknown",
                    born = "",
                    died = "",
                    titles = emptyList(),
                    aliases = emptyList(),
                    father = "",
                    mother = "",
                    spouse = "",
                    allegiances = emptyList(),
                    books = emptyList(),
                    povBooks = emptyList(),
                    tvSeries = emptyList(),
                    playedBy = emptyList(),
                )

            coEvery { mockRepository.getCharacterByUrl(characterUrl) } returns character
            coEvery { mockRepository.getBooksByUrls(emptyList()) } returns emptyList()

            // When
            val result = useCase.getCharacterWithBooks(characterUrl)

            // Then
            assertEquals(character, result?.character)
            assertEquals(0, result?.books?.size)
            assertEquals(0, result?.povBooks?.size)

            coVerify(exactly = 1) { mockRepository.getCharacterByUrl(characterUrl) }
            coVerify(exactly = 2) { mockRepository.getBooksByUrls(emptyList()) }
        }

    @Test
    fun `getCharacterWithBooks should handle character with only regular books`() =
        runTest {
            // Given
            val characterUrl = "https://anapioficeandfire.com/api/characters/100"
            val character =
                Character(
                    url = characterUrl,
                    name = "Cersei Lannister",
                    gender = "Female",
                    culture = "Westermen",
                    born = "In 266 AC",
                    died = "",
                    titles = listOf("Queen of the Seven Kingdoms"),
                    aliases = listOf("Light of the West"),
                    father = "",
                    mother = "",
                    spouse = "",
                    allegiances = listOf("House Lannister"),
                    books = listOf("https://anapioficeandfire.com/api/books/1"),
                    povBooks = emptyList(),
                    tvSeries = listOf("Season 1"),
                    playedBy = listOf("Lena Headey"),
                )

            val book =
                BookWithCover(
                    url = "https://anapioficeandfire.com/api/books/1",
                    name = "A Game of Thrones",
                    isbn = "978-0553103540",
                    authors = listOf("George R. R. Martin"),
                    numberOfPages = 694,
                    publisher = "Bantam Books",
                    country = "United States",
                    mediaType = "Hardcover",
                    released = "1996-08-01T00:00:00",
                    characters = emptyList(),
                    povCharacters = emptyList(),
                    coverImageUrl = "https://example.com/cover1.jpg",
                )

            coEvery { mockRepository.getCharacterByUrl(characterUrl) } returns character
            coEvery { mockRepository.getBooksByUrls(character.books) } returns listOf(book)
            coEvery { mockRepository.getBooksByUrls(emptyList()) } returns emptyList()

            // When
            val result = useCase.getCharacterWithBooks(characterUrl)

            // Then
            assertEquals(character, result?.character)
            assertEquals(1, result?.books?.size)
            assertEquals("A Game of Thrones", result?.books?.get(0)?.name)
            assertEquals(0, result?.povBooks?.size)
        }

    @Test
    fun `getCharacterWithBooks should propagate repository exceptions`() =
        runTest {
            // Given
            val characterUrl = "https://anapioficeandfire.com/api/characters/1"
            val exception = RuntimeException("Database error")

            coEvery { mockRepository.getCharacterByUrl(characterUrl) } throws exception

            // When & Then
            try {
                useCase.getCharacterWithBooks(characterUrl)
                assert(false) { "Expected exception to be thrown" }
            } catch (e: RuntimeException) {
                assertEquals("Database error", e.message)
            }

            coVerify(exactly = 1) { mockRepository.getCharacterByUrl(characterUrl) }
            coVerify(exactly = 0) { mockRepository.getBooksByUrls(any()) }
        }
}
