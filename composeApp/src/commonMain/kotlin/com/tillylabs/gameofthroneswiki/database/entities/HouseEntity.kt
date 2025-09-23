package com.tillylabs.gameofthroneswiki.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tillylabs.gameofthroneswiki.models.House
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Entity(tableName = "houses")
data class HouseEntity
    @OptIn(ExperimentalTime::class)
    constructor(
        @PrimaryKey val url: String,
        val name: String,
        val region: String,
        val coatOfArms: String,
        val words: String,
        val titles: List<String>,
        val seats: List<String>,
        val currentLord: String,
        val heir: String,
        val overlord: String,
        val founded: String,
        val founder: String,
        val diedOut: String,
        val ancestralWeapons: List<String>,
        val cadetBranches: List<String>,
        val swornMembers: List<String>,
        val lastUpdated: Long = Clock.System.now().toEpochMilliseconds(),
    )

fun HouseEntity.toHouse(): House =
    House(
        url = url,
        name = name,
        region = region,
        coatOfArms = coatOfArms,
        words = words,
        titles = titles,
        seats = seats,
        currentLord = currentLord,
        heir = heir,
        overlord = overlord,
        founded = founded,
        founder = founder,
        diedOut = diedOut,
        ancestralWeapons = ancestralWeapons,
        cadetBranches = cadetBranches,
        swornMembers = swornMembers,
    )

fun House.toHouseEntity(): HouseEntity =
    HouseEntity(
        url = url,
        name = name,
        region = region,
        coatOfArms = coatOfArms,
        words = words,
        titles = titles,
        seats = seats,
        currentLord = currentLord,
        heir = heir,
        overlord = overlord,
        founded = founded,
        founder = founder,
        diedOut = diedOut,
        ancestralWeapons = ancestralWeapons,
        cadetBranches = cadetBranches,
        swornMembers = swornMembers,
    )
