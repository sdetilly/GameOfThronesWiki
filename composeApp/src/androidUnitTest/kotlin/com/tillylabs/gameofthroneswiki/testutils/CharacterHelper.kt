package com.tillylabs.gameofthroneswiki.testutils

import com.tillylabs.gameofthroneswiki.models.Character

fun createCharacter(
    url: String = "https://anapioficeandfire.com/api/characters/1",
    name: String = "Test Character",
    gender: String = "Male",
    culture: String = "Test Culture",
    born: String = "In 283 AC",
    died: String = "",
    titles: List<String> = emptyList(),
    aliases: List<String> = emptyList(),
    father: String = "",
    mother: String = "",
    spouse: String = "",
    allegiances: List<String> = emptyList(),
    books: List<String> = emptyList(),
    povBooks: List<String> = emptyList(),
    tvSeries: List<String> = emptyList(),
    playedBy: List<String> = emptyList(),
) = Character(
    url = url,
    name = name,
    gender = gender,
    culture = culture,
    born = born,
    died = died,
    titles = titles,
    aliases = aliases,
    father = father,
    mother = mother,
    spouse = spouse,
    allegiances = allegiances,
    books = books,
    povBooks = povBooks,
    tvSeries = tvSeries,
    playedBy = playedBy,
)
