package com.tillylabs.gameofthroneswiki.http

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GameOfThronesHttpTest {
    @Test
    fun testFetchBooks() =
        runTest {
            val client = GameOfThronesHttp()

            try {
                val books = client.fetchBooks()

                assertNotNull(books, "Books should not be null")
                assertTrue(books.isNotEmpty(), "Books list should not be empty")
                assertTrue(books.size <= 50, "Books list should not exceed page size limit")

                val firstBook = books.first()
                assertNotNull(firstBook.name, "Book name should not be null")
                assertNotNull(firstBook.url, "Book URL should not be null")
                assertTrue(firstBook.url.contains("anapioficeandfire.com"), "Book URL should contain API domain")

                println("Successfully fetched ${books.size} books")
            } finally {
                client.close()
            }
        }

    @Test
    fun testFetchCharacters() =
        runTest {
            val client = GameOfThronesHttp()

            try {
                val characters = client.fetchCharacters(page = 1)

                assertNotNull(characters, "Characters should not be null")
                assertTrue(characters.isNotEmpty(), "Characters list should not be empty")
                assertTrue(characters.size <= 50, "Characters list should not exceed page size limit")

                val firstCharacter = characters.first()
                assertNotNull(firstCharacter.url, "Character URL should not be null")
                assertTrue(firstCharacter.url.contains("anapioficeandfire.com"), "Character URL should contain API domain")

                println("Successfully fetched ${characters.size} characters")
            } finally {
                client.close()
            }
        }

    @Test
    fun testFetchHouses() =
        runTest {
            val client = GameOfThronesHttp()

            try {
                val houses = client.fetchHouses(page = 1)

                assertNotNull(houses, "Houses should not be null")
                assertTrue(houses.isNotEmpty(), "Houses list should not be empty")
                assertTrue(houses.size <= 50, "Houses list should not exceed page size limit")

                val firstHouse = houses.first()
                assertNotNull(firstHouse.url, "House URL should not be null")
                assertTrue(firstHouse.url.contains("anapioficeandfire.com"), "House URL should contain API domain")

                println("Successfully fetched ${houses.size} houses")
            } finally {
                client.close()
            }
        }
}
