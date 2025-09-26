package com.tillylabs.gameofthroneswiki.database.entities

import com.tillylabs.gameofthroneswiki.models.House
import kotlin.test.Test
import kotlin.test.assertEquals

class HouseEntityTest {
    @Test
    fun `HouseEntity toHouse should map all fields correctly`() {
        // Given
        val houseEntity =
            HouseEntity(
                url = "https://anapioficeandfire.com/api/houses/362",
                name = "House Stark of Winterfell",
                region = "The North",
                coatOfArms = "A grey direwolf racing, on an ice-white field",
                words = "Winter is Coming",
                titles = listOf("King in the North", "Lord of Winterfell"),
                seats = listOf("Winterfell"),
                currentLord = "Sansa Stark",
                heir = "",
                overlord = "",
                founded = "",
                founder = "Brandon the Builder",
                diedOut = "",
                ancestralWeapons = listOf("Ice"),
                cadetBranches = listOf("House Karstark", "House Greystark"),
                swornMembers = listOf("House Karstark", "House Umber", "House Mormont"),
                lastUpdated = 1234567890L,
            )

        // When
        val house = houseEntity.toHouse()

        // Then
        assertEquals("https://anapioficeandfire.com/api/houses/362", house.url)
        assertEquals("House Stark of Winterfell", house.name)
        assertEquals("The North", house.region)
        assertEquals("A grey direwolf racing, on an ice-white field", house.coatOfArms)
        assertEquals("Winter is Coming", house.words)
        assertEquals(listOf("King in the North", "Lord of Winterfell"), house.titles)
        assertEquals(listOf("Winterfell"), house.seats)
        assertEquals("Sansa Stark", house.currentLord)
        assertEquals("", house.heir)
        assertEquals("", house.overlord)
        assertEquals("", house.founded)
        assertEquals("Brandon the Builder", house.founder)
        assertEquals("", house.diedOut)
        assertEquals(listOf("Ice"), house.ancestralWeapons)
        assertEquals(listOf("House Karstark", "House Greystark"), house.cadetBranches)
        assertEquals(listOf("House Karstark", "House Umber", "House Mormont"), house.swornMembers)
    }

    @Test
    fun `House toHouseEntity should map all fields correctly`() {
        // Given
        val house =
            House(
                url = "https://anapioficeandfire.com/api/houses/229",
                name = "House Lannister of Casterly Rock",
                region = "The Westerlands",
                coatOfArms = "A golden lion rampant, on a crimson field",
                words = "Hear Me Roar!",
                titles = listOf("Lord of Casterly Rock"),
                seats = listOf("Casterly Rock"),
                currentLord = "Tyrion Lannister",
                heir = "",
                overlord = "",
                founded = "",
                founder = "Lann the Clever",
                diedOut = "",
                ancestralWeapons = emptyList(),
                cadetBranches = listOf("House Lannister of Lannisport"),
                swornMembers = listOf("House Clegane", "House Payne", "House Swyft"),
            )

        // When
        val houseEntity = house.toHouseEntity()

        // Then
        assertEquals("https://anapioficeandfire.com/api/houses/229", houseEntity.url)
        assertEquals("House Lannister of Casterly Rock", houseEntity.name)
        assertEquals("The Westerlands", houseEntity.region)
        assertEquals("A golden lion rampant, on a crimson field", houseEntity.coatOfArms)
        assertEquals("Hear Me Roar!", houseEntity.words)
        assertEquals(listOf("Lord of Casterly Rock"), houseEntity.titles)
        assertEquals(listOf("Casterly Rock"), houseEntity.seats)
        assertEquals("Tyrion Lannister", houseEntity.currentLord)
        assertEquals("", houseEntity.heir)
        assertEquals("", houseEntity.overlord)
        assertEquals("", houseEntity.founded)
        assertEquals("Lann the Clever", houseEntity.founder)
        assertEquals("", houseEntity.diedOut)
        assertEquals(emptyList(), houseEntity.ancestralWeapons)
        assertEquals(listOf("House Lannister of Lannisport"), houseEntity.cadetBranches)
        assertEquals(listOf("House Clegane", "House Payne", "House Swyft"), houseEntity.swornMembers)
    }

    @Test
    fun `round trip conversion should preserve all data`() {
        // Given
        val originalHouse =
            House(
                url = "https://anapioficeandfire.com/api/houses/17",
                name = "House Targaryen of Dragonstone",
                region = "Crownlands",
                coatOfArms = "A three-headed dragon breathing flame, red on black",
                words = "Fire and Blood",
                titles = listOf("Prince of Dragonstone"),
                seats = listOf("Dragonstone"),
                currentLord = "",
                heir = "",
                overlord = "",
                founded = "",
                founder = "Aegon I Targaryen",
                diedOut = "",
                ancestralWeapons = listOf("Blackfyre", "Dark Sister"),
                cadetBranches = emptyList(),
                swornMembers = emptyList(),
            )

        // When
        val houseEntity = originalHouse.toHouseEntity()
        val resultHouse = houseEntity.toHouse()

        // Then
        assertEquals(originalHouse.url, resultHouse.url)
        assertEquals(originalHouse.name, resultHouse.name)
        assertEquals(originalHouse.region, resultHouse.region)
        assertEquals(originalHouse.coatOfArms, resultHouse.coatOfArms)
        assertEquals(originalHouse.words, resultHouse.words)
        assertEquals(originalHouse.titles, resultHouse.titles)
        assertEquals(originalHouse.seats, resultHouse.seats)
        assertEquals(originalHouse.currentLord, resultHouse.currentLord)
        assertEquals(originalHouse.heir, resultHouse.heir)
        assertEquals(originalHouse.overlord, resultHouse.overlord)
        assertEquals(originalHouse.founded, resultHouse.founded)
        assertEquals(originalHouse.founder, resultHouse.founder)
        assertEquals(originalHouse.diedOut, resultHouse.diedOut)
        assertEquals(originalHouse.ancestralWeapons, resultHouse.ancestralWeapons)
        assertEquals(originalHouse.cadetBranches, resultHouse.cadetBranches)
        assertEquals(originalHouse.swornMembers, resultHouse.swornMembers)
    }

    @Test
    fun `should handle empty strings and lists correctly`() {
        // Given
        val house =
            House(
                url = "https://anapioficeandfire.com/api/houses/1",
                name = "Test House",
                region = "",
                coatOfArms = "",
                words = "",
                titles = emptyList(),
                seats = emptyList(),
                currentLord = "",
                heir = "",
                overlord = "",
                founded = "",
                founder = "",
                diedOut = "",
                ancestralWeapons = emptyList(),
                cadetBranches = emptyList(),
                swornMembers = emptyList(),
            )

        // When
        val houseEntity = house.toHouseEntity()
        val resultHouse = houseEntity.toHouse()

        // Then
        assertEquals("", resultHouse.region)
        assertEquals("", resultHouse.coatOfArms)
        assertEquals("", resultHouse.words)
        assertEquals(emptyList(), resultHouse.titles)
        assertEquals(emptyList(), resultHouse.seats)
        assertEquals("", resultHouse.currentLord)
        assertEquals("", resultHouse.heir)
        assertEquals("", resultHouse.overlord)
        assertEquals("", resultHouse.founded)
        assertEquals("", resultHouse.founder)
        assertEquals("", resultHouse.diedOut)
        assertEquals(emptyList(), resultHouse.ancestralWeapons)
        assertEquals(emptyList(), resultHouse.cadetBranches)
        assertEquals(emptyList(), resultHouse.swornMembers)
    }
}
