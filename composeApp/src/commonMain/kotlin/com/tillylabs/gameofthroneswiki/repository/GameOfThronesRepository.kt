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

    private suspend fun loadBooksIfNeeded(lastUpdated: Long?) {
        lastUpdated ?: return
        if (lastUpdated + 1.minutes.inWholeMilliseconds < getCurrentTimeMillis()) {
            try {
                loadMoreBooks()
            } catch (e: Exception) {
            }
        }
    }

    private suspend fun loadMoreBooks() {
        val books = httpClient.fetchBooks(page = booksCurrentPage)

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

    fun getCharactersFlow(): Flow<List<Character>> {
        repositoryScope.launch {
            refreshCharactersFromApi()
        }

        return database
            .characterDao()
            .getAllCharacters()
            .map { entities -> entities.map { it.toCharacter() } }
    }

    suspend fun getCharacters(): List<Character> {
        val dbCharacters =
            database
                .characterDao()
                .getCharactersPaginated(50, 0)
                .map { it.toCharacter() }

        if (dbCharacters.isEmpty()) {
            refreshCharactersFromApi()
            return database
                .characterDao()
                .getCharactersPaginated(50, 0)
                .map { it.toCharacter() }
        }

        repositoryScope.launch {
            refreshCharactersFromApi()
        }

        return dbCharacters
    }

    suspend fun loadMoreCharacters(): List<Character> {
        val currentCount = database.characterDao().getCharactersCount()
        val newCharacters =
            database
                .characterDao()
                .getCharactersPaginated(50, currentCount)
                .map { it.toCharacter() }

        if (newCharacters.size < 50 && hasMoreCharacters) {
            refreshCharactersFromApi()
            return database
                .characterDao()
                .getCharactersPaginated(50, currentCount)
                .map { it.toCharacter() }
        }

        return newCharacters
    }

    private suspend fun refreshCharactersFromApi() {
        if (!hasMoreCharacters) return

        try {
            charactersCurrentPage++
            val apiCharacters = httpClient.fetchCharacters(page = charactersCurrentPage)

            if (apiCharacters.isEmpty()) {
                hasMoreCharacters = false
                return
            }

            val entities =
                apiCharacters
                    .filter { it.name.isNotEmpty() }
                    .map { it.toCharacterEntity() }

            database.characterDao().insertCharacters(entities)
        } catch (e: Exception) {
            println("Error refreshing characters from API: ${e.message}")
        }
    }

    fun getHousesFlow(): Flow<List<House>> {
        repositoryScope.launch {
            refreshHousesFromApi()
        }

        return database
            .houseDao()
            .getAllHouses()
            .map { entities -> entities.map { it.toHouse() } }
    }

    suspend fun getHouses(): List<House> {
        val dbHouses =
            database
                .houseDao()
                .getHousesPaginated(50, 0)
                .map { it.toHouse() }

        if (dbHouses.isEmpty()) {
            refreshHousesFromApi()
            return database
                .houseDao()
                .getHousesPaginated(50, 0)
                .map { it.toHouse() }
        }

        repositoryScope.launch {
            refreshHousesFromApi()
        }

        return dbHouses
    }

    suspend fun loadMoreHouses(): List<House> {
        val currentCount = database.houseDao().getHousesCount()
        val newHouses =
            database
                .houseDao()
                .getHousesPaginated(50, currentCount)
                .map { it.toHouse() }

        if (newHouses.size < 50 && hasMoreHouses) {
            refreshHousesFromApi()
            return database
                .houseDao()
                .getHousesPaginated(50, currentCount)
                .map { it.toHouse() }
        }

        return newHouses
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

        database.characterDao().deleteAllCharacters()
        database.houseDao().deleteAllHouses()
    }
}
