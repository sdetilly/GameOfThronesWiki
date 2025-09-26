package com.tillylabs.gameofthroneswiki.repository

import com.tillylabs.gameofthroneswiki.database.GameOfThronesDatabase
import com.tillylabs.gameofthroneswiki.database.dao.BookDao
import com.tillylabs.gameofthroneswiki.database.dao.CharacterDao
import com.tillylabs.gameofthroneswiki.database.dao.HouseDao
import com.tillylabs.gameofthroneswiki.database.entities.BookEntity
import com.tillylabs.gameofthroneswiki.database.entities.CharacterEntity
import com.tillylabs.gameofthroneswiki.database.entities.HouseEntity
import com.tillylabs.gameofthroneswiki.database.entities.toCharacter
import com.tillylabs.gameofthroneswiki.http.GameOfThronesHttp
import com.tillylabs.gameofthroneswiki.models.Book
import com.tillylabs.gameofthroneswiki.models.BookWithCover
import com.tillylabs.gameofthroneswiki.models.House
import com.tillylabs.gameofthroneswiki.testutils.createCharacter
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
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

            coEvery { mockHttpClient.fetchBooks(0) } returns listOf(book)
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
            delay(100)

            // Verify API was called in background
            coVerify(exactly = 1) { mockHttpClient.fetchBooks(0) }
            coVerify(exactly = 1) { mockHttpClient.fetchBookCover(book.isbn) }
        }

    @Test
    fun `getCharacters should return from database first then refresh from API`() =
        runTest {
            // Given
            val expectedCharacter =
                createCharacter(
                    url = "https://anapioficeandfire.com/api/characters/1",
                    name = "Jon Snow",
                    culture = "Northmen",
                    titles = listOf("Lord Commander of the Night's Watch"),
                    aliases = listOf("Lord Snow"),
                )
            val characterEntity =
                CharacterEntity(
                    url = expectedCharacter.url,
                    name = expectedCharacter.name,
                    gender = expectedCharacter.gender,
                    culture = expectedCharacter.culture,
                    born = expectedCharacter.born,
                    died = expectedCharacter.died,
                    titles = expectedCharacter.titles,
                    aliases = expectedCharacter.aliases,
                    father = expectedCharacter.father,
                    mother = expectedCharacter.mother,
                    spouse = expectedCharacter.spouse,
                    allegiances = expectedCharacter.allegiances,
                    books = expectedCharacter.books,
                    povBooks = expectedCharacter.povBooks,
                    tvSeries = expectedCharacter.tvSeries,
                    playedBy = expectedCharacter.playedBy,
                    imageUrl = null,
                )

            // Database returns cached character
            coEvery { mockCharacterDao.getCharactersPaginated(50, 0) } returns listOf(characterEntity)
            coEvery { mockHttpClient.fetchCharacters(1) } returns listOf(expectedCharacter)
            coEvery { mockCharacterDao.insertCharacters(any()) } returns Unit

            // When
            val result = repository.getCharacters()

            // Then
            assertEquals(listOf(expectedCharacter), result)
            coVerify { mockCharacterDao.getCharactersPaginated(50, 0) }
        }

    @Test
    fun `getHouses should return from database first then refresh from API`() =
        runTest {
            // Given
            val expectedHouse =
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
                )
            val houseEntity =
                HouseEntity(
                    url = expectedHouse.url,
                    name = expectedHouse.name,
                    region = expectedHouse.region,
                    coatOfArms = expectedHouse.coatOfArms,
                    words = expectedHouse.words,
                    titles = expectedHouse.titles,
                    seats = expectedHouse.seats,
                    currentLord = expectedHouse.currentLord,
                    heir = expectedHouse.heir,
                    overlord = expectedHouse.overlord,
                    founded = expectedHouse.founded,
                    founder = expectedHouse.founder,
                    diedOut = expectedHouse.diedOut,
                    ancestralWeapons = expectedHouse.ancestralWeapons,
                    cadetBranches = expectedHouse.cadetBranches,
                    swornMembers = expectedHouse.swornMembers,
                )

            // Database returns cached house
            coEvery { mockHouseDao.getHousesPaginated(50, 0) } returns listOf(houseEntity)
            coEvery { mockHttpClient.fetchHouses(1) } returns listOf(expectedHouse)
            coEvery { mockHouseDao.insertHouses(any()) } returns Unit

            // When
            val result = repository.getHouses()

            // Then
            assertEquals(listOf(expectedHouse), result)
            coVerify { mockHouseDao.getHousesPaginated(50, 0) }
        }

    @Test
    fun `clearCache should clear database and in-memory cache`() =
        runTest {
            // Given
            coEvery { mockCharacterDao.deleteAllCharacters() } returns Unit
            coEvery { mockHouseDao.deleteAllHouses() } returns Unit

            // When
            repository.clearCache()

            // Then
            coVerify { mockCharacterDao.deleteAllCharacters() }
            coVerify { mockHouseDao.deleteAllHouses() }
        }

    @Test
    fun `getCharacters should fetch from API when database is empty`() =
        runTest {
            // Given
            val expectedCharacter =
                createCharacter(
                    url = "https://anapioficeandfire.com/api/characters/1",
                    name = "Jon Snow",
                    culture = "Northmen",
                    titles = listOf("Lord Commander of the Night's Watch"),
                    aliases = listOf("Lord Snow"),
                )

            // Database is empty initially, then returns the character after API call
            coEvery { mockCharacterDao.getCharactersPaginated(50, 0) } returnsMany
                listOf(
                    emptyList(), // First call - empty database
                    listOf(
                        CharacterEntity(
                            url = expectedCharacter.url,
                            name = expectedCharacter.name,
                            gender = expectedCharacter.gender,
                            culture = expectedCharacter.culture,
                            born = expectedCharacter.born,
                            died = expectedCharacter.died,
                            titles = expectedCharacter.titles,
                            aliases = expectedCharacter.aliases,
                            father = expectedCharacter.father,
                            mother = expectedCharacter.mother,
                            spouse = expectedCharacter.spouse,
                            allegiances = expectedCharacter.allegiances,
                            books = expectedCharacter.books,
                            povBooks = expectedCharacter.povBooks,
                            tvSeries = expectedCharacter.tvSeries,
                            playedBy = expectedCharacter.playedBy,
                            imageUrl = null,
                        ),
                    ), // Second call - after API data is saved
                )
            coEvery { mockHttpClient.fetchCharacters(1) } returns listOf(expectedCharacter)
            coEvery { mockCharacterDao.insertCharacters(any()) } returns Unit

            // When
            val result = repository.getCharacters()

            // Then
            assertEquals(listOf(expectedCharacter), result)
            coVerify { mockHttpClient.fetchCharacters(1) }
            coVerify { mockCharacterDao.insertCharacters(any()) }
        }

    @Test
    fun `getCharactersFlow should return Flow from database`() =
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

            every { mockCharacterDao.getAllCharacters() } returns flowOf(listOf(characterEntity))
            coEvery { mockHttpClient.fetchCharacters(1) } returns listOf(characterEntity.toCharacter())
            coEvery { mockCharacterDao.insertCharacters(any()) } returns Unit

            // When
            val flow = repository.getCharactersFlow()

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

            coEvery { mockHttpClient.fetchBooks(0) } throws expectedException

            // Mock the database flow to return empty list (no cached data)
            every { mockBookDao.getAllBooks() } returns flowOf(emptyList())

            // When - API error should not affect the flow
            val result = repository.getBooks()

            // Then - should return empty database result without throwing
            assertEquals(emptyList(), result.first())

            // Give background coroutine time to complete
            delay(100)

            // Verify API was called and failed, but flow continued
            coVerify(exactly = 1) { mockHttpClient.fetchBooks(0) }
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

            coEvery { mockHttpClient.fetchBooks(0) } returns listOf(bookWithoutIsbn)
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
            delay(100)

            // Verify API was called but no cover fetch for empty ISBN
            coVerify(exactly = 1) { mockHttpClient.fetchBooks(0) }
            coVerify(exactly = 0) { mockHttpClient.fetchBookCover(any()) }
        }
}
