package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.models.BookWithCover
import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.koin.core.annotation.Factory
import kotlin.time.Duration.Companion.days

interface BooksUseCase {
    suspend fun booksWithCover(): List<BookWithCover>
}

@Factory
class BooksUseCaseImpl(
    private val repository: GameOfThronesRepository,
) : BooksUseCase {
    override suspend fun booksWithCover(): List<BookWithCover> = repository.getBooks()
}

class BooksUseCasePreview(
    private val previewState: PreviewState = PreviewState.DATA,
) : BooksUseCase {
    override suspend fun booksWithCover(): List<BookWithCover> =
        coroutineScope {
            when (previewState) {
                PreviewState.DATA ->
                    listOf(
                        BookWithCover(
                            url = "a",
                            name = "A Game of Thrones",
                            isbn = "978-0553103540",
                            authors = listOf("George R. R. Martin"),
                            numberOfPages = 694,
                            publisher = "Bantam Books",
                            country = "United States",
                            mediaType = "Hardcover",
                            released = "1996-08-01",
                            characters = listOf("Greyjoy", "Arya Stark", "Ned Stark"),
                            povCharacters = listOf("Greyjoy", "Arya Stark", "Ned Stark"),
                            coverImageUrl = "https://covers.openlibrary.org/b/isbn/9780553103540-L.jpg",
                        ),
                        BookWithCover(
                            url = "b",
                            name = "A Clash of Kings",
                            isbn = "978-0553108033",
                            authors = listOf("George R. R. Martin"),
                            numberOfPages = 768,
                            publisher = "Bantam Books",
                            country = "United States",
                            mediaType = "Hardcover",
                            released = "1998-11-16",
                            characters = listOf("Greyjoy", "Arya Stark", "Ned Stark"),
                            povCharacters = listOf("Greyjoy", "Arya Stark", "Ned Stark"),
                            coverImageUrl = "https://covers.openlibrary.org/b/isbn/9780553108033-L.jpg",
                        ),
                        BookWithCover(
                            "c",
                            name = "A Storm of Swords",
                            isbn = "978-0553106633",
                            authors = listOf("George R. R. Martin"),
                            numberOfPages = 973,
                            publisher = "Bantam Books",
                            country = "United States",
                            mediaType = "Hardcover",
                            released = "2000-10-31",
                            characters = listOf("Greyjoy", "Arya Stark", "Ned Stark"),
                            povCharacters = listOf("Greyjoy", "Arya Stark", "Ned Stark"),
                            coverImageUrl = "https://covers.openlibrary.org/b/isbn/9780553106633-L.jpg",
                        ),
                    )
                PreviewState.EMPTY -> emptyList()
                PreviewState.LOADING -> {
                    delay(1.days)
                    emptyList()
                }
                PreviewState.ERROR -> throw Exception("Failed to load books")
            }
        }
}
