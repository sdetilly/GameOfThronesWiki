package com.tillylabs.gameofthroneswiki.http

import com.tillylabs.gameofthroneswiki.models.Book
import com.tillylabs.gameofthroneswiki.models.BookCoverResponse
import com.tillylabs.gameofthroneswiki.models.Character
import com.tillylabs.gameofthroneswiki.models.House
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class GameOfThronesHttp {
    private val httpClient =
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    },
                )
            }
        }

    private companion object {
        const val BASE_URL = "https://www.anapioficeandfire.com/api"
        const val BOOK_COVER_BASE_URL = "https://bookcover.longitood.com/bookcover"
        const val PAGE_SIZE = 50
    }

    suspend fun fetchBooks(): List<Book> =
        httpClient
            .get("$BASE_URL/books") {
                parameter("pageSize", PAGE_SIZE)
            }.body()

    suspend fun fetchCharacters(page: Int = 1): List<Character> =
        httpClient
            .get("$BASE_URL/characters") {
                parameter("page", page)
                parameter("pageSize", PAGE_SIZE)
            }.body()

    suspend fun fetchHouses(page: Int = 1): List<House> =
        httpClient
            .get("$BASE_URL/houses") {
                parameter("page", page)
                parameter("pageSize", PAGE_SIZE)
            }.body()

    suspend fun fetchBookByUrl(url: String): Book? =
        try {
            httpClient.get(url).body()
        } catch (e: Exception) {
            null
        }

    suspend fun fetchBookCover(isbn: String): String? =
        try {
            httpClient
                .get("$BOOK_COVER_BASE_URL/$isbn")
                .body<BookCoverResponse>()
                .url
        } catch (e: Exception) {
            null
        }

    fun close() {
        httpClient.close()
    }
}
