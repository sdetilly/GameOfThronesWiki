package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.models.House
import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory
import kotlin.time.Duration.Companion.days

interface HousesUseCase {
    fun housesFlow(): Flow<List<House>>

    suspend fun houses(): List<House>

    suspend fun loadMore(): List<House>

    fun hasMore(): Boolean
}

@Factory
class HousesUseCaseImpl(
    private val repository: GameOfThronesRepository,
) : HousesUseCase {
    override fun housesFlow(): Flow<List<House>> = repository.getHousesFlow()

    override suspend fun houses(): List<House> = repository.getHouses()

    override suspend fun loadMore(): List<House> = repository.loadMoreHouses()

    override fun hasMore(): Boolean = repository.hasMoreHouses()
}

class HousesUseCasePreview(
    private val previewState: PreviewState = PreviewState.DATA,
) : HousesUseCase {
    override fun housesFlow(): Flow<List<House>> =
        kotlinx.coroutines.flow.flowOf(
            when (previewState) {
                PreviewState.DATA -> getPreviewHouses()
                PreviewState.EMPTY -> emptyList()
                PreviewState.LOADING -> emptyList()
                PreviewState.ERROR -> emptyList()
            },
        )

    private fun getPreviewHouses() =
        listOf(
            House(
                url = "https://www.anapioficeandfire.com/api/houses/362",
                name = "House Stark of Winterfell",
                region = "The North",
                coatOfArms = "A grey direwolf on a white field",
                words = "Winter is Coming",
                titles = listOf("King in the North", "Lord of Winterfell", "Warden of the North"),
                seats = listOf("Winterfell"),
                currentLord = "Eddard Stark",
                heir = "Robb Stark",
                overlord = "House Baratheon of King's Landing",
                founded = "Age of Heroes",
                founder = "Brandon the Builder",
                diedOut = "",
                ancestralWeapons = listOf("Ice"),
                cadetBranches = listOf(),
                swornMembers = listOf("Jon Snow", "Arya Stark", "Sansa Stark", "Bran Stark", "Rickon Stark"),
            ),
            House(
                url = "https://www.anapioficeandfire.com/api/houses/229",
                name = "House Lannister of Casterly Rock",
                region = "The Westerlands",
                coatOfArms = "A golden lion on a crimson field",
                words = "Hear Me Roar",
                titles = listOf("Lord of Casterly Rock", "Shield of Lannisport", "Warden of the West"),
                seats = listOf("Casterly Rock"),
                currentLord = "Tywin Lannister",
                heir = "Jaime Lannister",
                overlord = "House Baratheon of King's Landing",
                founded = "Age of Heroes",
                founder = "Lann the Clever",
                diedOut = "",
                ancestralWeapons = listOf("Brightroar"),
                cadetBranches = listOf("House Lannister of Lannisport"),
                swornMembers = listOf("Tyrion Lannister", "Cersei Lannister", "Jaime Lannister"),
            ),
            House(
                url = "https://www.anapioficeandfire.com/api/houses/378",
                name = "House Targaryen of King's Landing",
                region = "The Crownlands",
                coatOfArms = "A red three-headed dragon on a black field",
                words = "Fire and Blood",
                titles =
                    listOf(
                        "King of the Andals, the Rhoynar, and the First Men",
                        "Lord of the Seven Kingdoms",
                        "Protector of the Realm",
                    ),
                seats = listOf("King's Landing", "Dragonstone"),
                currentLord = "Daenerys Targaryen",
                heir = "",
                overlord = "",
                founded = "114 BC",
                founder = "Aegon I Targaryen",
                diedOut = "",
                ancestralWeapons = listOf("Blackfyre", "Dark Sister"),
                cadetBranches = listOf("House Blackfyre"),
                swornMembers = listOf("Daenerys Targaryen", "Viserys Targaryen"),
            ),
            House(
                url = "https://www.anapioficeandfire.com/api/houses/17",
                name = "House Baratheon of Storm's End",
                region = "The Stormlands",
                coatOfArms = "A crowned black stag on a golden field",
                words = "Ours is the Fury",
                titles = listOf("Lord of Storm's End", "Lord Paramount of the Stormlands"),
                seats = listOf("Storm's End"),
                currentLord = "Robert Baratheon",
                heir = "Joffrey Baratheon",
                overlord = "",
                founded = "War of Conquest",
                founder = "Orys Baratheon",
                diedOut = "",
                ancestralWeapons = listOf(),
                cadetBranches = listOf("House Baratheon of King's Landing", "House Baratheon of Dragonstone"),
                swornMembers = listOf("Robert Baratheon", "Stannis Baratheon", "Renly Baratheon"),
            ),
            House(
                url = "https://www.anapioficeandfire.com/api/houses/395",
                name = "House Tully of Riverrun",
                region = "The Riverlands",
                coatOfArms = "A silver trout leaping on a blue and red striped field",
                words = "Family, Duty, Honor",
                titles = listOf("Lord of Riverrun", "Lord Paramount of the Trident"),
                seats = listOf("Riverrun"),
                currentLord = "Hoster Tully",
                heir = "Edmure Tully",
                overlord = "House Baratheon of King's Landing",
                founded = "Age of Heroes",
                founder = "",
                diedOut = "",
                ancestralWeapons = listOf(),
                cadetBranches = listOf(),
                swornMembers = listOf("Catelyn Stark", "Lysa Arryn", "Edmure Tully"),
            ),
        )

    override suspend fun houses(): List<House> =
        coroutineScope {
            when (previewState) {
                PreviewState.DATA -> getPreviewHouses()
                PreviewState.EMPTY -> emptyList()
                PreviewState.LOADING -> {
                    delay(1.days)
                    emptyList()
                }
                PreviewState.ERROR -> throw Exception("Failed to load characters")
            }
        }

    override suspend fun loadMore(): List<House> = emptyList()

    override fun hasMore(): Boolean = false
}
