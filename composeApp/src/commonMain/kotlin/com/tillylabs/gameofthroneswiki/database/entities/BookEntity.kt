package com.tillylabs.gameofthroneswiki.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tillylabs.gameofthroneswiki.models.BookWithCover
import com.tillylabs.gameofthroneswiki.utils.getCurrentTimeMillis

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val url: String,
    val name: String,
    val isbn: String,
    val authors: List<String>,
    val numberOfPages: Int,
    val publisher: String,
    val country: String,
    val mediaType: String,
    val released: String,
    val characters: List<String>,
    val povCharacters: List<String>,
    val coverImageUrl: String?,
    val lastUpdated: Long = getCurrentTimeMillis(),
)

fun BookEntity.toBookWithCover(): BookWithCover =
    BookWithCover(
        url = url,
        name = name,
        isbn = isbn,
        authors = authors,
        numberOfPages = numberOfPages,
        publisher = publisher,
        country = country,
        mediaType = mediaType,
        released = released,
        characters = characters,
        povCharacters = povCharacters,
        coverImageUrl = coverImageUrl,
    )

fun BookWithCover.toBookEntity(): BookEntity =
    BookEntity(
        url = url,
        name = name,
        isbn = isbn,
        authors = authors,
        numberOfPages = numberOfPages,
        publisher = publisher,
        country = country,
        mediaType = mediaType,
        released = released,
        characters = characters,
        povCharacters = povCharacters,
        coverImageUrl = coverImageUrl,
    )
