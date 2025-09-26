package com.tillylabs.gameofthroneswiki.database.entities

import com.tillylabs.gameofthroneswiki.models.BookWithCover
import kotlin.test.Test
import kotlin.test.assertEquals

class BookEntityTest {
    @Test
    fun `BookEntity toBookWithCover should map all fields correctly`() {
        // Given
        val bookEntity =
            BookEntity(
                url = "https://anapioficeandfire.com/api/books/1",
                name = "A Game of Thrones",
                isbn = "978-0553103540",
                authors = listOf("George R. R. Martin"),
                numberOfPages = 694,
                publisher = "Bantam Books",
                country = "United States",
                mediaType = "Hardcover",
                released = "1996-08-01T00:00:00",
                characters = listOf("character1", "character2"),
                povCharacters = listOf("pov1", "pov2"),
                coverImageUrl = "https://example.com/cover.jpg",
                lastUpdated = 1234567890L,
            )

        // When
        val bookWithCover = bookEntity.toBookWithCover()

        // Then
        assertEquals("https://anapioficeandfire.com/api/books/1", bookWithCover.url)
        assertEquals("A Game of Thrones", bookWithCover.name)
        assertEquals("978-0553103540", bookWithCover.isbn)
        assertEquals(listOf("George R. R. Martin"), bookWithCover.authors)
        assertEquals(694, bookWithCover.numberOfPages)
        assertEquals("Bantam Books", bookWithCover.publisher)
        assertEquals("United States", bookWithCover.country)
        assertEquals("Hardcover", bookWithCover.mediaType)
        assertEquals("1996-08-01T00:00:00", bookWithCover.released)
        assertEquals(listOf("character1", "character2"), bookWithCover.characters)
        assertEquals(listOf("pov1", "pov2"), bookWithCover.povCharacters)
        assertEquals("https://example.com/cover.jpg", bookWithCover.coverImageUrl)
    }

    @Test
    fun `BookWithCover toBookEntity should map all fields correctly`() {
        // Given
        val bookWithCover =
            BookWithCover(
                url = "https://anapioficeandfire.com/api/books/2",
                name = "A Clash of Kings",
                isbn = "978-0553108033",
                authors = listOf("George R. R. Martin"),
                numberOfPages = 761,
                publisher = "Bantam Books",
                country = "United States",
                mediaType = "Hardcover",
                released = "1999-02-01T00:00:00",
                characters = listOf("character3", "character4"),
                povCharacters = listOf("pov3", "pov4"),
                coverImageUrl = null,
            )

        // When
        val bookEntity = bookWithCover.toBookEntity()

        // Then
        assertEquals("https://anapioficeandfire.com/api/books/2", bookEntity.url)
        assertEquals("A Clash of Kings", bookEntity.name)
        assertEquals("978-0553108033", bookEntity.isbn)
        assertEquals(listOf("George R. R. Martin"), bookEntity.authors)
        assertEquals(761, bookEntity.numberOfPages)
        assertEquals("Bantam Books", bookEntity.publisher)
        assertEquals("United States", bookEntity.country)
        assertEquals("Hardcover", bookEntity.mediaType)
        assertEquals("1999-02-01T00:00:00", bookEntity.released)
        assertEquals(listOf("character3", "character4"), bookEntity.characters)
        assertEquals(listOf("pov3", "pov4"), bookEntity.povCharacters)
        assertEquals(null, bookEntity.coverImageUrl)
    }

    @Test
    fun `round trip conversion should preserve all data`() {
        // Given
        val originalBookWithCover =
            BookWithCover(
                url = "https://anapioficeandfire.com/api/books/3",
                name = "A Storm of Swords",
                isbn = "",
                authors = emptyList(),
                numberOfPages = 992,
                publisher = "Bantam Books",
                country = "United States",
                mediaType = "Hardcover",
                released = "2000-08-08T00:00:00",
                characters = emptyList(),
                povCharacters = emptyList(),
                coverImageUrl = "https://example.com/storm.jpg",
            )

        // When
        val bookEntity = originalBookWithCover.toBookEntity()
        val resultBookWithCover = bookEntity.toBookWithCover()

        // Then
        assertEquals(originalBookWithCover.url, resultBookWithCover.url)
        assertEquals(originalBookWithCover.name, resultBookWithCover.name)
        assertEquals(originalBookWithCover.isbn, resultBookWithCover.isbn)
        assertEquals(originalBookWithCover.authors, resultBookWithCover.authors)
        assertEquals(originalBookWithCover.numberOfPages, resultBookWithCover.numberOfPages)
        assertEquals(originalBookWithCover.publisher, resultBookWithCover.publisher)
        assertEquals(originalBookWithCover.country, resultBookWithCover.country)
        assertEquals(originalBookWithCover.mediaType, resultBookWithCover.mediaType)
        assertEquals(originalBookWithCover.released, resultBookWithCover.released)
        assertEquals(originalBookWithCover.characters, resultBookWithCover.characters)
        assertEquals(originalBookWithCover.povCharacters, resultBookWithCover.povCharacters)
        assertEquals(originalBookWithCover.coverImageUrl, resultBookWithCover.coverImageUrl)
    }
}
