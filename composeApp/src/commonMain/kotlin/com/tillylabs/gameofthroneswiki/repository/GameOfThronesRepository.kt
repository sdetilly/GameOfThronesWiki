package com.tillylabs.gameofthroneswiki.repository

import com.tillylabs.gameofthroneswiki.database.GameOfThronesDatabase
import com.tillylabs.gameofthroneswiki.database.entities.toBookEntity
import com.tillylabs.gameofthroneswiki.database.entities.toBookWithCover
import com.tillylabs.gameofthroneswiki.database.entities.toCharacter
import com.tillylabs.gameofthroneswiki.database.entities.toCharacterEntity
import com.tillylabs.gameofthroneswiki.database.entities.toHouse
import com.tillylabs.gameofthroneswiki.database.entities.toHouseEntity
import com.tillylabs.gameofthroneswiki.http.GameOfThronesHttp
import com.tillylabs.gameofthroneswiki.models.BookWithCover
import com.tillylabs.gameofthroneswiki.models.Character
import com.tillylabs.gameofthroneswiki.models.House
import com.tillylabs.gameofthroneswiki.models.toBookWithCover
import com.tillylabs.gameofthroneswiki.utils.getCurrentTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.minutes

@Single
class GameOfThronesRepository(
    private val httpClient: GameOfThronesHttp,
    private val database: GameOfThronesDatabase,
) {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var booksCurrentPage = 0
    private var charactersCurrentPage = 0
    private var housesCurrentPage = 0

    private var hasMoreCharacters = true
    private var hasMoreHouses = true

    fun getBooks(): Flow<List<BookWithCover>> {
        repositoryScope.launch {
            loadBooksIfNeeded(database.bookDao().getLastUpdatedTimestamp())
        }

        return database
            .bookDao()
            .getAllBooks()
            .map { entities -> entities.map { it.toBookWithCover() } }
    }

    suspend fun getBookByUrl(url: String): BookWithCover? = database.bookDao().getBookByUrl(url)?.toBookWithCover()

    suspend fun getCharacterByUrl(url: String): Character? = database.characterDao().getCharacterByUrl(url)?.toCharacter()

    suspend fun getBooksByUrls(urls: List<String>): List<BookWithCover> {
        if (urls.isEmpty()) return emptyList()

        val existingBooks = database.bookDao().getBooksByUrls(urls).map { it.toBookWithCover() }
        val existingUrls = existingBooks.map { it.url }.toSet()

        val missingUrls = urls.filter { it !in existingUrls }

        if (missingUrls.isNotEmpty()) {
            val fetchedBooks =
                missingUrls.mapNotNull { url ->
                    try {
                        val book = httpClient.fetchBookByUrl(url) ?: return@mapNotNull null
                        val coverUrl =
                            if (book.isbn.isNotBlank()) {
                                httpClient.fetchBookCover(book.isbn)
                            } else {
                                null
                            }
                        book.toBookWithCover(coverUrl)
                    } catch (e: Exception) {
                        println("Error fetching book $url: ${e.message}")
                        null
                    }
                }

            if (fetchedBooks.isNotEmpty()) {
                database.bookDao().insertBooks(fetchedBooks.map { it.toBookEntity() })
            }

            return existingBooks + fetchedBooks
        }

        return existingBooks
    }

    private suspend fun loadBooksIfNeeded(lastUpdated: Long?) {
        val shouldLoad =
            lastUpdated == null ||
                lastUpdated + 1.minutes.inWholeMilliseconds < getCurrentTimeMillis()

        if (shouldLoad) {
            try {
                loadMoreBooks()
            } catch (e: Exception) {
                println("Error loading books: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private suspend fun loadMoreBooks() {
        val books = httpClient.fetchBooks()

        val booksWithCovers =
            coroutineScope {
                books
                    .map { book ->
                        async {
                            val coverUrl =
                                if (book.isbn.isNotBlank()) {
                                    httpClient.fetchBookCover(book.isbn)
                                } else {
                                    null
                                }
                            book.toBookWithCover(coverUrl)
                        }
                    }.map { it.await() }
            }
        database.bookDao().insertBooks(booksWithCovers.map { it.toBookEntity() })
    }

    fun getCharacters(): Flow<List<Character>> {
        repositoryScope.launch {
            refreshCharactersFromApi()
        }

        return database
            .characterDao()
            .getAllCharacters()
            .map { entities -> entities.map { it.toCharacter() } }
    }

    suspend fun loadMoreCharacters(): List<Character> {
        val currentCount = database.characterDao().getCharactersCount()

        val dbCharacters =
            database
                .characterDao()
                .getCharactersPaginated(50, currentCount)
                .map { it.toCharacter() }

        repositoryScope.launch {
            if (hasMoreCharacters) {
                try {
                    refreshCharactersFromApi()
                } catch (e: Exception) {
                    println("Error loading more characters: ${e.message}")
                }
            }
        }

        if (dbCharacters.isNotEmpty()) {
            return dbCharacters
        }

        if (hasMoreCharacters) {
            try {
                refreshCharactersFromApi()
                return database
                    .characterDao()
                    .getCharactersPaginated(50, currentCount)
                    .map { it.toCharacter() }
            } catch (e: Exception) {
                println("Error loading more characters: ${e.message}")
                return emptyList()
            }
        }

        return emptyList()
    }

    suspend fun refreshCharacters() {
        try {
            refreshCharactersFromApi()
        } catch (e: Exception) {
            println("Error refreshing characters: ${e.message}")
        }
    }

    private suspend fun refreshCharactersFromApi() {
        try {
            val apiCharacters = httpClient.fetchCharacters(page = charactersCurrentPage)

            val entities =
                apiCharacters
                    .filter { it.name.isNotEmpty() }
                    .map { it.toCharacterEntity() }

            database.characterDao().insertCharacters(entities)
        } catch (e: Exception) {
            println("Error refreshing characters from API: ${e.message}")
        }
    }

    fun getHouses(): Flow<List<House>> {
        repositoryScope.launch {
            refreshHousesFromApi()
        }

        return database
            .houseDao()
            .getAllHouses()
            .map { entities -> entities.map { it.toHouse() } }
    }

    suspend fun loadMoreHouses(): List<House> {
        val currentCount = database.houseDao().getHousesCount()

        val dbHouses =
            database
                .houseDao()
                .getHousesPaginated(50, currentCount)
                .map { it.toHouse() }

        repositoryScope.launch {
            if (hasMoreHouses) {
                try {
                    refreshHousesFromApi()
                } catch (e: Exception) {
                    println("Error loading more houses: ${e.message}")
                }
            }
        }

        if (dbHouses.isNotEmpty()) {
            return dbHouses
        }

        if (hasMoreHouses) {
            try {
                refreshHousesFromApi()
                return database
                    .houseDao()
                    .getHousesPaginated(50, currentCount)
                    .map { it.toHouse() }
            } catch (e: Exception) {
                println("Error loading more houses: ${e.message}")
                return emptyList()
            }
        }

        return emptyList()
    }

    suspend fun refreshHouses() {
        try {
            refreshHousesFromApi()
        } catch (e: Exception) {
            println("Error refreshing houses: ${e.message}")
        }
    }

    private suspend fun refreshHousesFromApi() {
        if (!hasMoreHouses) return

        try {
            housesCurrentPage++
            val apiHouses = httpClient.fetchHouses(page = housesCurrentPage)

            if (apiHouses.isEmpty()) {
                hasMoreHouses = false
                return
            }

            val entities = apiHouses.map { it.toHouseEntity() }
            database.houseDao().insertHouses(entities)
        } catch (e: Exception) {
            println("Error refreshing houses from API: ${e.message}")
        }
    }

    fun hasMoreCharacters(): Boolean = hasMoreCharacters

    fun hasMoreHouses(): Boolean = hasMoreHouses

    suspend fun clearCache() {
        booksCurrentPage = 0
        charactersCurrentPage = 0
        housesCurrentPage = 0

        hasMoreCharacters = true
        hasMoreHouses = true

        database.bookDao().deleteAllBooks()
        database.characterDao().deleteAllCharacters()
        database.houseDao().deleteAllHouses()
    }
}
