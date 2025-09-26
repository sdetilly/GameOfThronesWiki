package com.tillylabs.gameofthroneswiki.database.converters

import kotlin.test.Test
import kotlin.test.assertEquals

class ConvertersTest {
    private val converters = Converters()

    @Test
    fun `fromStringList should convert list to JSON string`() {
        // Given
        val stringList = listOf("Jon Snow", "Tyrion Lannister", "Daenerys Targaryen")

        // When
        val result = converters.fromStringList(stringList)

        // Then
        assertEquals("""["Jon Snow","Tyrion Lannister","Daenerys Targaryen"]""", result)
    }

    @Test
    fun `toStringList should convert JSON string to list`() {
        // Given
        val jsonString = """["Jon Snow","Tyrion Lannister","Daenerys Targaryen"]"""

        // When
        val result = converters.toStringList(jsonString)

        // Then
        assertEquals(listOf("Jon Snow", "Tyrion Lannister", "Daenerys Targaryen"), result)
    }

    @Test
    fun `fromStringList should handle empty list`() {
        // Given
        val emptyList = emptyList<String>()

        // When
        val result = converters.fromStringList(emptyList)

        // Then
        assertEquals("[]", result)
    }

    @Test
    fun `toStringList should handle empty JSON array`() {
        // Given
        val emptyJsonArray = "[]"

        // When
        val result = converters.toStringList(emptyJsonArray)

        // Then
        assertEquals(emptyList(), result)
    }

    @Test
    fun `round trip conversion should preserve data`() {
        // Given
        val originalList = listOf("A Game of Thrones", "A Clash of Kings", "A Storm of Swords")

        // When
        val jsonString = converters.fromStringList(originalList)
        val resultList = converters.toStringList(jsonString)

        // Then
        assertEquals(originalList, resultList)
    }
}
