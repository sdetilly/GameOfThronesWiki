package com.tillylabs.gameofthroneswiki.database.entities

import com.tillylabs.gameofthroneswiki.models.Character
import kotlin.test.Test
import kotlin.test.assertEquals

class CharacterEntityTest {
    @Test
    fun `CharacterEntity toCharacter should map all fields correctly`() {
        // Given
        val characterEntity =
            CharacterEntity(
                url = "https://anapioficeandfire.com/api/characters/583",
                name = "Jon Snow",
                gender = "Male",
                culture = "Northmen",
                born = "In 283 AC",
                died = "",
                titles = listOf("Lord Commander of the Night's Watch"),
                aliases = listOf("Lord Snow", "Ned Stark's Bastard"),
                father = "",
                mother = "",
                spouse = "",
                allegiances = listOf("House Stark", "Night's Watch"),
                books = listOf("A Game of Thrones"),
                povBooks = listOf("A Dance with Dragons"),
                tvSeries = listOf("Season 1", "Season 2"),
                playedBy = listOf("Kit Harington"),
                imageUrl = "https://example.com/jon.jpg",
                lastUpdated = 1234567890L,
            )

        // When
        val character = characterEntity.toCharacter()

        // Then
        assertEquals("https://anapioficeandfire.com/api/characters/583", character.url)
        assertEquals("Jon Snow", character.name)
        assertEquals("Male", character.gender)
        assertEquals("Northmen", character.culture)
        assertEquals("In 283 AC", character.born)
        assertEquals("", character.died)
        assertEquals(listOf("Lord Commander of the Night's Watch"), character.titles)
        assertEquals(listOf("Lord Snow", "Ned Stark's Bastard"), character.aliases)
        assertEquals("", character.father)
        assertEquals("", character.mother)
        assertEquals("", character.spouse)
        assertEquals(listOf("House Stark", "Night's Watch"), character.allegiances)
        assertEquals(listOf("A Game of Thrones"), character.books)
        assertEquals(listOf("A Dance with Dragons"), character.povBooks)
        assertEquals(listOf("Season 1", "Season 2"), character.tvSeries)
        assertEquals(listOf("Kit Harington"), character.playedBy)
    }

    @Test
    fun `Character toCharacterEntity should map all fields correctly`() {
        // Given
        val character =
            Character(
                url = "https://anapioficeandfire.com/api/characters/238",
                name = "Tyrion Lannister",
                gender = "Male",
                culture = "Westeros",
                born = "In 273 AC",
                died = "",
                titles = listOf("Hand of the King"),
                aliases = listOf("The Imp", "Halfman"),
                father = "Tywin Lannister",
                mother = "Joanna Lannister",
                spouse = "Sansa Stark",
                allegiances = listOf("House Lannister"),
                books = emptyList(),
                povBooks = listOf("A Game of Thrones", "A Clash of Kings"),
                tvSeries = listOf("Season 1"),
                playedBy = listOf("Peter Dinklage"),
            )
        val imageUrl = "https://example.com/tyrion.jpg"

        // When
        val characterEntity = character.toCharacterEntity(imageUrl)

        // Then
        assertEquals("https://anapioficeandfire.com/api/characters/238", characterEntity.url)
        assertEquals("Tyrion Lannister", characterEntity.name)
        assertEquals("Male", characterEntity.gender)
        assertEquals("Westeros", characterEntity.culture)
        assertEquals("In 273 AC", characterEntity.born)
        assertEquals("", characterEntity.died)
        assertEquals(listOf("Hand of the King"), characterEntity.titles)
        assertEquals(listOf("The Imp", "Halfman"), characterEntity.aliases)
        assertEquals("Tywin Lannister", characterEntity.father)
        assertEquals("Joanna Lannister", characterEntity.mother)
        assertEquals("Sansa Stark", characterEntity.spouse)
        assertEquals(listOf("House Lannister"), characterEntity.allegiances)
        assertEquals(emptyList(), characterEntity.books)
        assertEquals(listOf("A Game of Thrones", "A Clash of Kings"), characterEntity.povBooks)
        assertEquals(listOf("Season 1"), characterEntity.tvSeries)
        assertEquals(listOf("Peter Dinklage"), characterEntity.playedBy)
        assertEquals("https://example.com/tyrion.jpg", characterEntity.imageUrl)
    }

    @Test
    fun `Character toCharacterEntity with null imageUrl should set imageUrl to null`() {
        // Given
        val character =
            Character(
                url = "https://anapioficeandfire.com/api/characters/1",
                name = "Test Character",
                gender = "Male",
                culture = "",
                born = "",
                died = "",
                titles = emptyList(),
                aliases = emptyList(),
                father = "",
                mother = "",
                spouse = "",
                allegiances = emptyList(),
                books = emptyList(),
                povBooks = emptyList(),
                tvSeries = emptyList(),
                playedBy = emptyList(),
            )

        // When
        val characterEntity = character.toCharacterEntity()

        // Then
        assertEquals(null, characterEntity.imageUrl)
    }

    @Test
    fun `round trip conversion should preserve all data`() {
        // Given
        val originalCharacter =
            Character(
                url = "https://anapioficeandfire.com/api/characters/148",
                name = "Arya Stark",
                gender = "Female",
                culture = "Northmen",
                born = "In 289 AC",
                died = "",
                titles = emptyList(),
                aliases = listOf("Arry", "No One"),
                father = "Eddard Stark",
                mother = "Catelyn Stark",
                spouse = "",
                allegiances = listOf("House Stark"),
                books = listOf("A Game of Thrones"),
                povBooks = listOf("A Clash of Kings", "A Storm of Swords"),
                tvSeries = listOf("Season 1", "Season 2"),
                playedBy = listOf("Maisie Williams"),
            )

        // When
        val characterEntity = originalCharacter.toCharacterEntity("https://example.com/arya.jpg")
        val resultCharacter = characterEntity.toCharacter()

        // Then
        assertEquals(originalCharacter.url, resultCharacter.url)
        assertEquals(originalCharacter.name, resultCharacter.name)
        assertEquals(originalCharacter.gender, resultCharacter.gender)
        assertEquals(originalCharacter.culture, resultCharacter.culture)
        assertEquals(originalCharacter.born, resultCharacter.born)
        assertEquals(originalCharacter.died, resultCharacter.died)
        assertEquals(originalCharacter.titles, resultCharacter.titles)
        assertEquals(originalCharacter.aliases, resultCharacter.aliases)
        assertEquals(originalCharacter.father, resultCharacter.father)
        assertEquals(originalCharacter.mother, resultCharacter.mother)
        assertEquals(originalCharacter.spouse, resultCharacter.spouse)
        assertEquals(originalCharacter.allegiances, resultCharacter.allegiances)
        assertEquals(originalCharacter.books, resultCharacter.books)
        assertEquals(originalCharacter.povBooks, resultCharacter.povBooks)
        assertEquals(originalCharacter.tvSeries, resultCharacter.tvSeries)
        assertEquals(originalCharacter.playedBy, resultCharacter.playedBy)
    }
}
