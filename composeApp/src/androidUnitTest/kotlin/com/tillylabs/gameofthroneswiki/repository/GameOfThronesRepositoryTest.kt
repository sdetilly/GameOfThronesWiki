package com.tillylabs.gameofthroneswiki.repository

import com.tillylabs.gameofthroneswiki.database.GameOfThronesDatabase
import com.tillylabs.gameofthroneswiki.database.dao.BookDao
import com.tillylabs.gameofthroneswiki.database.dao.CharacterDao
import com.tillylabs.gameofthroneswiki.database.dao.HouseDao
import com.tillylabs.gameofthroneswiki.database.entities.BookEntity
import com.tillylabs.gameofthroneswiki.database.entities.CharacterEntity
import com.tillylabs.gameofthroneswiki.database.entities.HouseEntity
import com.tillylabs.gameofthroneswiki.database.entities.toCharacter
import com.tillylabs.gameofthroneswiki.database.entities.toHouse
import com.tillylabs.gameofthroneswiki.http.GameOfThronesHttp
import com.tillylabs.gameofthroneswiki.models.Book
import com.tillylabs.gameofthroneswiki.models.BookWithCover
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GameOfThronesRepositoryTest {
    private val mockHttpClient = mockk<GameOfThronesHttp>()
    private val mockDatabase = mockk<GameOfThronesDatabase>()
    private val mockBookDao = mockk<BookDao>()
    private val mockCharacterDao = mockk<CharacterDao>()
    private val mockHouseDao = mockk<HouseDao>()
    private val repository = GameOfThronesRepository(mockHttpClient, mockDatabase)

    init {
        every { mockDatabase.bookDao() } returns mockBookDao
        every { mockDatabase.characterDao() } returns mockCharacterDao
        every { mockDatabase.houseDao() } returns mockHouseDao

        // Mock the getLastUpdatedTimestamp method for all tests
        coEvery { mockBookDao.getLastUpdatedTimestamp() } returns null
    }

    @Test
    fun `getBooks should fetch from API and cache result`() =
        runTest {
            // Given
            val book =
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
                )
            val expectedCoverUrl = "https://example.com/cover.jpg"
            val expectedBooksWithCover =
                listOf(
                    BookWithCover(
                        url = book.url,
                        name = book.name,
                        isbn = book.isbn,
                        authors = book.authors,
                        numberOfPages = book.numberOfPages,
                        publisher = book.publisher,
                        country = book.country,
                        mediaType = book.mediaType,
                        released = book.released,
                        characters = book.characters,
                        povCharacters = book.povCharacters,
                        coverImageUrl = expectedCoverUrl,
                    ),
                )

            // Mock an old timestamp to trigger API refresh
            coEvery { mockBookDao.getLastUpdatedTimestamp() } returns System.currentTimeMillis() - 120000 // 2 minutes ago

            coEvery { mockHttpClient.fetchBooks() } returns listOf(book)
            coEvery { mockBookDao.insertBooks(any()) } returns Unit
            coEvery { mockHttpClient.fetchBookCover(book.isbn) } returns expectedCoverUrl

            // Mock the database flow to return the expected book entities
            val expectedBookEntity =
                BookEntity(
                    url = book.url,
                    name = book.name,
                    isbn = book.isbn,
                    authors = book.authors,
                    numberOfPages = book.numberOfPages,
                    publisher = book.publisher,
                    country = book.country,
                    mediaType = book.mediaType,
                    released = book.released,
                    characters = book.characters,
                    povCharacters = book.povCharacters,
                    coverImageUrl = expectedCoverUrl,
                )
            every { mockBookDao.getAllBooks() } returns flowOf(listOf(expectedBookEntity))

            // When - first call
            val result1 = repository.getBooks()

            // Then - should return database result immediately
            assertEquals(expectedBooksWithCover, result1.first())

            // Give background coroutine time to complete
            advanceUntilIdle()

            // Verify API was called in background
            coVerify(exactly = 1) { mockHttpClient.fetchBooks() }
            coVerify(exactly = 1) { mockHttpClient.fetchBookCover(book.isbn) }
        }

    @Test
    fun `clearCache should clear database and in-memory cache`() =
        runTest {
            // Given
            coEvery { mockBookDao.deleteAllBooks() } returns Unit
            coEvery { mockCharacterDao.deleteAllCharacters() } returns Unit
            coEvery { mockHouseDao.deleteAllHouses() } returns Unit

            // When
            repository.clearCache()

            // Then
            coVerify { mockBookDao.deleteAllBooks() }
            coVerify { mockCharacterDao.deleteAllCharacters() }
            coVerify { mockHouseDao.deleteAllHouses() }
        }

    @Test
    fun `getCharacters should return Flow from database`() =
        runTest {
            // Given
            val characterEntity =
                CharacterEntity(
                    url = "https://anapioficeandfire.com/api/characters/1",
                    name = "Jon Snow",
                    gender = "Male",
                    culture = "Northmen",
                    born = "283 AC",
                    died = "",
                    titles = listOf("Lord Commander of the Night's Watch"),
                    aliases = listOf("Lord Snow"),
                    father = "",
                    mother = "",
                    spouse = "",
                    allegiances = listOf("House Stark"),
                    books = emptyList(),
                    povBooks = emptyList(),
                    tvSeries = emptyList(),
                    playedBy = listOf("Kit Harington"),
                    imageUrl = null,
                )

            coEvery { mockCharacterDao.getCharactersCount() } returns 0
            every { mockCharacterDao.getAllCharacters() } returns flowOf(listOf(characterEntity))
            coEvery { mockHttpClient.fetchCharacters(1) } returns listOf(characterEntity.toCharacter())
            coEvery { mockCharacterDao.insertCharacters(any()) } returns Unit

            // When
            val flow = repository.getCharacters()

            // Then
            flow.collect { characters ->
                assertEquals(1, characters.size)
                assertEquals("Jon Snow", characters[0].name)
            }
        }

    @Test
    fun `repository should handle API exceptions gracefully`() =
        runTest {
            // Given
            val expectedException = RuntimeException("API Error")

            // Mock an old timestamp to trigger API refresh
            coEvery { mockBookDao.getLastUpdatedTimestamp() } returns System.currentTimeMillis() - 120000 // 2 minutes ago

            coEvery { mockHttpClient.fetchBooks() } throws expectedException

            // Mock the database flow to return empty list (no cached data)
            every { mockBookDao.getAllBooks() } returns flowOf(emptyList())

            // When - API error should not affect the flow
            val result = repository.getBooks()

            // Then - should return empty database result without throwing
            assertEquals(emptyList(), result.first())

            // Give background coroutine time to complete
            advanceUntilIdle()

            // Verify API was called and failed, but flow continued
            coVerify(exactly = 1) { mockHttpClient.fetchBooks() }
        }

    @Test
    fun `getBooks should handle books without ISBN`() =
        runTest {
            // Given
            val bookWithoutIsbn =
                Book(
                    url = "https://anapioficeandfire.com/api/books/1",
                    name = "Book Without ISBN",
                    isbn = "",
                    authors = listOf("Test Author"),
                    numberOfPages = 100,
                    publisher = "Test Publisher",
                    country = "Test Country",
                    mediaType = "Hardcover",
                    released = "2000-01-01T00:00:00",
                    characters = emptyList(),
                    povCharacters = emptyList(),
                )
            val expectedBookWithCover =
                BookWithCover(
                    url = bookWithoutIsbn.url,
                    name = bookWithoutIsbn.name,
                    isbn = bookWithoutIsbn.isbn,
                    authors = bookWithoutIsbn.authors,
                    numberOfPages = bookWithoutIsbn.numberOfPages,
                    publisher = bookWithoutIsbn.publisher,
                    country = bookWithoutIsbn.country,
                    mediaType = bookWithoutIsbn.mediaType,
                    released = bookWithoutIsbn.released,
                    characters = bookWithoutIsbn.characters,
                    povCharacters = bookWithoutIsbn.povCharacters,
                    coverImageUrl = null,
                )

            // Mock an old timestamp to trigger API refresh
            coEvery { mockBookDao.getLastUpdatedTimestamp() } returns System.currentTimeMillis() - 120000 // 2 minutes ago

            coEvery { mockHttpClient.fetchBooks() } returns listOf(bookWithoutIsbn)
            coEvery { mockBookDao.insertBooks(any()) } returns Unit

            // Mock the database flow to return the expected book entity
            val expectedBookEntity =
                BookEntity(
                    url = bookWithoutIsbn.url,
                    name = bookWithoutIsbn.name,
                    isbn = bookWithoutIsbn.isbn,
                    authors = bookWithoutIsbn.authors,
                    numberOfPages = bookWithoutIsbn.numberOfPages,
                    publisher = bookWithoutIsbn.publisher,
                    country = bookWithoutIsbn.country,
                    mediaType = bookWithoutIsbn.mediaType,
                    released = bookWithoutIsbn.released,
                    characters = bookWithoutIsbn.characters,
                    povCharacters = bookWithoutIsbn.povCharacters,
                    coverImageUrl = null,
                )
            every { mockBookDao.getAllBooks() } returns flowOf(listOf(expectedBookEntity))

            // When
            val result = repository.getBooks()

            // Then - should return database result immediately
            assertEquals(listOf(expectedBookWithCover), result.first())

            // Give background coroutine time to complete
            advanceUntilIdle()

            // Verify API was called but no cover fetch for empty ISBN
            coVerify(exactly = 1) { mockHttpClient.fetchBooks() }
            coVerify(exactly = 0) { mockHttpClient.fetchBookCover(any()) }
        }

    @Test
    fun `loadMoreCharacters should return database characters and refresh in background`() =
        runTest {
            // Given
            val characterEntity =
                CharacterEntity(
                    url = "https://anapioficeandfire.com/api/characters/1",
                    name = "Test Character",
                    gender = "Male",
                    culture = "Northmen",
                    born = "283 AC",
                    died = "",
                    titles = listOf("Lord"),
                    aliases = listOf("Test"),
                    father = "",
                    mother = "",
                    spouse = "",
                    allegiances = listOf("House Test"),
                    books = emptyList(),
                    povBooks = emptyList(),
                    tvSeries = emptyList(),
                    playedBy = emptyList(),
                    imageUrl = null,
                )

            coEvery { mockCharacterDao.getCharactersCount() } returns 0
            coEvery { mockCharacterDao.getCharactersPaginated(50, 0) } returns listOf(characterEntity)
            coEvery { mockHttpClient.fetchCharacters(0) } returns listOf(characterEntity.toCharacter())
            coEvery { mockCharacterDao.insertCharacters(any()) } returns Unit

            // When
            val result = repository.loadMoreCharacters()

            // Then
            assertEquals(1, result.size)
            assertEquals("Test Character", result[0].name)

            // Give background coroutine time to complete
            advanceUntilIdle()

            // Verify API was called in background at least once
            coVerify(atLeast = 1) { mockHttpClient.fetchCharacters(0) }
        }

    @Test
    fun `loadMoreHouses should return database houses and refresh in background`() =
        runTest {
            // Given
            val houseEntity =
                HouseEntity(
                    url = "https://anapioficeandfire.com/api/houses/1",
                    name = "Test House",
                    region = "North",
                    coatOfArms = "A test",
                    words = "Test Words",
                    titles = listOf("Lord"),
                    seats = listOf("Test Castle"),
                    currentLord = "",
                    heir = "",
                    overlord = "",
                    founded = "",
                    founder = "",
                    diedOut = "",
                    ancestralWeapons = emptyList(),
                    cadetBranches = emptyList(),
                    swornMembers = emptyList(),
                )

            coEvery { mockHouseDao.getHousesCount() } returns 0
            coEvery { mockHouseDao.getHousesPaginated(50, 0) } returns listOf(houseEntity)
            coEvery { mockHttpClient.fetchHouses(1) } returns listOf(houseEntity.toHouse())
            coEvery { mockHouseDao.insertHouses(any()) } returns Unit

            // When
            val result = repository.loadMoreHouses()

            // Then
            assertEquals(1, result.size)
            assertEquals("Test House", result[0].name)

            // Give background coroutine time to complete
            advanceUntilIdle()

            // Verify API was called in background
            coVerify(exactly = 1) { mockHttpClient.fetchHouses(1) }
        }

    @Test
    fun `refreshCharacters should handle API exceptions gracefully`() =
        runTest {
            // Given
            coEvery { mockHttpClient.fetchCharacters(0) } throws RuntimeException("API Error")

            // When - should not throw exception
            repository.refreshCharacters()

            // Then
            coVerify(exactly = 1) { mockHttpClient.fetchCharacters(0) }
            // No database insertion should happen due to exception
            coVerify(exactly = 0) { mockCharacterDao.insertCharacters(any()) }
        }

    @Test
    fun `refreshHouses should handle API exceptions gracefully`() =
        runTest {
            // Given
            coEvery { mockHttpClient.fetchHouses(1) } throws RuntimeException("API Error")

            // When - should not throw exception
            repository.refreshHouses()

            // Then
            coVerify(exactly = 1) { mockHttpClient.fetchHouses(1) }
            // No database insertion should happen due to exception
            coVerify(exactly = 0) { mockHouseDao.insertHouses(any()) }
        }

    @Test
    fun `hasMoreCharacters should return true by default`() {
        // Then
        assertEquals(true, repository.hasMoreCharacters())
    }

    @Test
    fun `hasMoreHouses should return true by default`() {
        // Then
        assertEquals(true, repository.hasMoreHouses())
    }

    @Test
    fun `clearCache should reset all state and clear database`() =
        runTest {
            // Given
            coEvery { mockBookDao.deleteAllBooks() } returns Unit
            coEvery { mockCharacterDao.deleteAllCharacters() } returns Unit
            coEvery { mockHouseDao.deleteAllHouses() } returns Unit

            // When
            repository.clearCache()

            // Then
            coVerify { mockBookDao.deleteAllBooks() }
            coVerify { mockCharacterDao.deleteAllCharacters() }
            coVerify { mockHouseDao.deleteAllHouses() }
            // Note: The hasMore methods should return true after clearing cache
            assertEquals(true, repository.hasMoreCharacters())
            assertEquals(true, repository.hasMoreHouses())
        }

    @Test
    fun `loadMoreCharacters should return empty list when no database results and API fails`() =
        runTest {
            // Given
            coEvery { mockCharacterDao.getCharactersCount() } returns 0
            coEvery { mockCharacterDao.getCharactersPaginated(50, 0) } returns emptyList()
            coEvery { mockHttpClient.fetchCharacters(0) } throws RuntimeException("API Error")

            // When
            val result = repository.loadMoreCharacters()

            // Then - should fallback to immediate API call and return empty list on error
            assertEquals(emptyList(), result)

            // Give background coroutine time to complete
            advanceUntilIdle()

            // Verify API was called at least once (once in background, once immediate)
            coVerify(atLeast = 1) { mockHttpClient.fetchCharacters(0) }
        }

    @Test
    fun `loadMoreHouses should return empty list when no database results and API fails`() =
        runTest {
            // Given
            coEvery { mockHouseDao.getHousesCount() } returns 0
            coEvery { mockHouseDao.getHousesPaginated(50, 0) } returns emptyList()
            coEvery { mockHttpClient.fetchHouses(1) } throws RuntimeException("API Error")

            // When
            val result = repository.loadMoreHouses()

            // Then - should fallback to immediate API call and return empty list on error
            assertEquals(emptyList(), result)
            coVerify(exactly = 1) { mockHttpClient.fetchHouses(1) }
        }
}
