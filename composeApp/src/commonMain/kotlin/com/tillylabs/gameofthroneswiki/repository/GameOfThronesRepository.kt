package com.tillylabs.gameofthroneswiki.repository

import com.tillylabs.gameofthroneswiki.http.GameOfThronesHttp
import com.tillylabs.gameofthroneswiki.models.BookWithCover
import com.tillylabs.gameofthroneswiki.models.Character
import com.tillylabs.gameofthroneswiki.models.House
import com.tillylabs.gameofthroneswiki.models.toBookWithCover
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.koin.core.annotation.Single

@Single
class GameOfThronesRepository(
    private val httpClient: GameOfThronesHttp,
) {
    private var cachedBooks: MutableList<BookWithCover> = mutableListOf()
    private var cachedCharacters: MutableList<Character> = mutableListOf()
    private var cachedHouses: MutableList<House> = mutableListOf()

    private var booksCurrentPage = 0
    private var charactersCurrentPage = 0
    private var housesCurrentPage = 0

    private var hasMoreBooks = true
    private var hasMoreCharacters = true
    private var hasMoreHouses = true

    suspend fun getBooks(): List<BookWithCover> {
        if (cachedBooks.isEmpty()) {
            loadMoreBooks()
        }
        return cachedBooks
    }

    suspend fun loadMoreBooks(): List<BookWithCover> {
        if (!hasMoreBooks) return cachedBooks

        booksCurrentPage++
        val books = httpClient.fetchBooks(page = booksCurrentPage)

        if (books.isEmpty()) {
            hasMoreBooks = false
            return cachedBooks
        }

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

        cachedBooks.addAll(booksWithCovers)
        return cachedBooks
    }

    suspend fun getCharacters(): List<Character> {
        if (cachedCharacters.isEmpty()) {
            loadMoreCharacters()
        }
        return cachedCharacters
    }

    suspend fun loadMoreCharacters(): List<Character> {
        if (!hasMoreCharacters) return cachedCharacters

        charactersCurrentPage++
        val characters = httpClient.fetchCharacters(page = charactersCurrentPage)

        if (characters.isEmpty()) {
            hasMoreCharacters = false
            return cachedCharacters
        }

        cachedCharacters.addAll(characters)
        return cachedCharacters
    }

    suspend fun getHouses(): List<House> {
        if (cachedHouses.isEmpty()) {
            loadMoreHouses()
        }
        return cachedHouses
    }

    suspend fun loadMoreHouses(): List<House> {
        if (!hasMoreHouses) return cachedHouses

        housesCurrentPage++
        val houses = httpClient.fetchHouses(page = housesCurrentPage)

        if (houses.isEmpty()) {
            hasMoreHouses = false
            return cachedHouses
        }

        cachedHouses.addAll(houses)
        return cachedHouses
    }

    fun hasMoreBooks(): Boolean = hasMoreBooks

    fun hasMoreCharacters(): Boolean = hasMoreCharacters

    fun hasMoreHouses(): Boolean = hasMoreHouses

    fun clearCache() {
        cachedBooks.clear()
        cachedCharacters.clear()
        cachedHouses.clear()

        booksCurrentPage = 0
        charactersCurrentPage = 0
        housesCurrentPage = 0

        hasMoreBooks = true
        hasMoreCharacters = true
        hasMoreHouses = true
    }
}
