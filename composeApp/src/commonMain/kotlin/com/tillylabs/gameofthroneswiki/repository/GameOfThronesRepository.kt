package com.tillylabs.gameofthroneswiki.repository

import com.tillylabs.gameofthroneswiki.http.GameOfThronesHttp
import com.tillylabs.gameofthroneswiki.models.Book
import com.tillylabs.gameofthroneswiki.models.Character
import com.tillylabs.gameofthroneswiki.models.House
import org.koin.core.annotation.Single

@Single
class GameOfThronesRepository(
    private val httpClient: GameOfThronesHttp,
) {
    private var cachedBooks: List<Book>? = null
    private var cachedCharacters: List<Character>? = null
    private var cachedHouses: List<House>? = null

    suspend fun getBooks(): List<Book> =
        cachedBooks ?: run {
            val books = httpClient.fetchBooks(page = 1)
            cachedBooks = books
            books
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
