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
    private var cachedBooks: List<BookWithCover>? = null
    private var cachedCharacters: List<Character>? = null
    private var cachedHouses: List<House>? = null

    suspend fun getBooks(): List<BookWithCover> =
        cachedBooks ?: run {
            val books = httpClient.fetchBooks(page = 1)
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
            cachedBooks = booksWithCovers
            booksWithCovers
        }

    suspend fun getCharacters(): List<Character> =
        cachedCharacters ?: run {
            val characters = httpClient.fetchCharacters(page = 1)
            cachedCharacters = characters
            characters
        }

    suspend fun getHouses(): List<House> =
        cachedHouses ?: run {
            val houses = httpClient.fetchHouses(page = 1)
            cachedHouses = houses
            houses
        }

    fun clearCache() {
        cachedBooks = null
        cachedCharacters = null
        cachedHouses = null
    }
}
