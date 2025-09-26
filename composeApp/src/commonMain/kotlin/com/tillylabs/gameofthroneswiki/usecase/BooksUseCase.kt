package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.models.BookWithCover
import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.Factory

interface BooksUseCase {
    fun booksWithCover(): Flow<List<BookWithCover>>
}

@Factory
class BooksUseCaseImpl(
    private val repository: GameOfThronesRepository,
) : BooksUseCase {
    override fun booksWithCover(): Flow<List<BookWithCover>> = repository.getBooks().onEach { println("SDET: Got some books!") }
}

class BooksUseCasePreview(
    private val previewState: PreviewState = PreviewState.DATA,
) : BooksUseCase {
    override fun booksWithCover(): Flow<List<BookWithCover>> =
        when (previewState) {
            PreviewState.DATA ->
                flowOf(
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
                    ),
                )
            PreviewState.EMPTY -> flowOf(emptyList())
            PreviewState.LOADING -> emptyFlow()
            PreviewState.ERROR -> throw Exception("Failed to load books")
        }
}
