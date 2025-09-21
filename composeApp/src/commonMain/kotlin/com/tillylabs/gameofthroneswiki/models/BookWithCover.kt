package com.tillylabs.gameofthroneswiki.models

data class BookWithCover(
    val url: String,
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
)

fun Book.toBookWithCover(coverImageUrl: String? = null) =
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
