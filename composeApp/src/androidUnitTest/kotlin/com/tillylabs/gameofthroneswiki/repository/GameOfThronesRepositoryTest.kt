package com.tillylabs.gameofthroneswiki.repository

import com.tillylabs.gameofthroneswiki.http.GameOfThronesHttp
import com.tillylabs.gameofthroneswiki.models.Book
import com.tillylabs.gameofthroneswiki.models.Character
import com.tillylabs.gameofthroneswiki.models.House
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class GameOfThronesRepositoryTest {
    private val mockHttpClient = mockk<GameOfThronesHttp>()
    private val repository = GameOfThronesRepository(mockHttpClient)

    @Test
    fun `getBooks should fetch from API and cache result`() =
        runTest {
            // Given
            val expectedBooks =
                listOf(
                    Book(
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
                    ),
                )
            coEvery { mockHttpClient.fetchBooks(1) } returns expectedBooks

            // When - first call
            val result1 = repository.getBooks()

            // Then - should fetch from API
            assertEquals(expectedBooks, result1)
            coVerify(exactly = 1) { mockHttpClient.fetchBooks(1) }

            // When - second call
            val result2 = repository.getBooks()

            // Then - should return cached result without API call
            assertEquals(expectedBooks, result2)
            assertSame(result1, result2) // Same instance from cache
            coVerify(exactly = 1) { mockHttpClient.fetchBooks(1) } // Still only one API call
        }

    @Test
    fun `getCharacters should fetch from API and cache result`() =
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
                )
            coEvery { mockHttpClient.fetchCharacters(1) } returns expectedCharacters

            // When - first call
            val result1 = repository.getCharacters()

            // Then
            assertEquals(expectedCharacters, result1)
            coVerify(exactly = 1) { mockHttpClient.fetchCharacters(1) }

            // When - second call should use cache
            val result2 = repository.getCharacters()
            assertSame(result1, result2)
            coVerify(exactly = 1) { mockHttpClient.fetchCharacters(1) }
        }

    @Test
    fun `getHouses should fetch from API and cache result`() =
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
                )
            coEvery { mockHttpClient.fetchHouses(1) } returns expectedHouses

            // When - first call
            val result1 = repository.getHouses()

            // Then
            assertEquals(expectedHouses, result1)
            coVerify(exactly = 1) { mockHttpClient.fetchHouses(1) }

            // When - second call should use cache
            val result2 = repository.getHouses()
            assertSame(result1, result2)
            coVerify(exactly = 1) { mockHttpClient.fetchHouses(1) }
        }

    @Test
    fun `clearCache should reset all cached data`() =
        runTest {
            // Given - setup cached data
            val books = listOf(mockk<Book>())
            val characters = listOf(mockk<Character>())
            val houses = listOf(mockk<House>())

            coEvery { mockHttpClient.fetchBooks(1) } returns books
            coEvery { mockHttpClient.fetchCharacters(1) } returns characters
            coEvery { mockHttpClient.fetchHouses(1) } returns houses

            // Cache all data
            repository.getBooks()
            repository.getCharacters()
            repository.getHouses()

            // When - clear cache
            repository.clearCache()

            // Then - next calls should fetch from API again
            val newBooks = listOf(mockk<Book>())
            val newCharacters = listOf(mockk<Character>())
            val newHouses = listOf(mockk<House>())

            coEvery { mockHttpClient.fetchBooks(1) } returns newBooks
            coEvery { mockHttpClient.fetchCharacters(1) } returns newCharacters
            coEvery { mockHttpClient.fetchHouses(1) } returns newHouses

            val resultBooks = repository.getBooks()
            val resultCharacters = repository.getCharacters()
            val resultHouses = repository.getHouses()

            assertNotSame(books, resultBooks)
            assertNotSame(characters, resultCharacters)
            assertNotSame(houses, resultHouses)

            coVerify(exactly = 2) { mockHttpClient.fetchBooks(1) }
            coVerify(exactly = 2) { mockHttpClient.fetchCharacters(1) }
            coVerify(exactly = 2) { mockHttpClient.fetchHouses(1) }
        }

    @Test
    fun `repository should propagate API exceptions`() =
        runTest {
            // Given
            val expectedException = RuntimeException("API Error")
            coEvery { mockHttpClient.fetchBooks(1) } throws expectedException

            // When & Then
            try {
                repository.getBooks()
                throw AssertionError("Should have thrown exception")
            } catch (e: Exception) {
                assertEquals(expectedException, e)
            }
        }
}
