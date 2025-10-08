package com.tillylabs.gameofthroneswiki.models

data class CharacterWithBooks(
    val character: Character,
    val books: List<BookWithCover>,
    val povBooks: List<BookWithCover>,
)
