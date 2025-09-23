package com.tillylabs.gameofthroneswiki.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tillylabs.gameofthroneswiki.models.Character
import com.tillylabs.gameofthroneswiki.utils.getCurrentTimeMillis

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val url: String,
    val name: String,
    val gender: String,
    val culture: String,
    val born: String,
    val died: String,
    val titles: List<String>,
    val aliases: List<String>,
    val father: String,
    val mother: String,
    val spouse: String,
    val allegiances: List<String>,
    val books: List<String>,
    val povBooks: List<String>,
    val tvSeries: List<String>,
    val playedBy: List<String>,
    val imageUrl: String?,
    val lastUpdated: Long = getCurrentTimeMillis(),
)

fun CharacterEntity.toCharacter(): Character =
    Character(
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

fun Character.toCharacterEntity(imageUrl: String? = null): CharacterEntity =
    CharacterEntity(
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
        imageUrl = imageUrl,
    )
