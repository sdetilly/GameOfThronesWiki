package com.tillylabs.gameofthroneswiki.usecase

import com.tillylabs.gameofthroneswiki.models.Character
import com.tillylabs.gameofthroneswiki.repository.GameOfThronesRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

interface CharactersUseCase {
    fun characters(): Flow<List<Character>>

    suspend fun refreshCharacters()

    suspend fun loadMore(): List<Character>

    fun hasMore(): Boolean
}

@Factory
class CharactersUseCaseImpl(
    private val repository: GameOfThronesRepository,
) : CharactersUseCase {
    override fun characters(): Flow<List<Character>> = repository.getCharacters()

    override suspend fun refreshCharacters() = repository.refreshCharacters()

    override suspend fun loadMore(): List<Character> =
        repository
            .loadMoreCharacters()
            .filter { it.name.isNotEmpty() }

    override fun hasMore(): Boolean = repository.hasMoreCharacters()
}

class CharactersUseCasePreview(
    private val previewState: PreviewState = PreviewState.DATA,
) : CharactersUseCase {
    override fun characters(): Flow<List<Character>> =
        kotlinx.coroutines.flow.flowOf(
            when (previewState) {
                PreviewState.DATA -> getPreviewCharacters()
                PreviewState.EMPTY -> emptyList()
                PreviewState.LOADING -> emptyList()
                PreviewState.ERROR -> emptyList()
            },
        )

    private fun getPreviewCharacters() =
        listOf(
            Character(
                url = "https://www.anapioficeandfire.com/api/characters/583",
                name = "Jon Snow",
                gender = "Male",
                culture = "Northmen",
                born = "In 283 AC",
                died = "",
                titles = listOf("Lord Commander of the Night's Watch", "King in the North"),
                aliases = listOf("Lord Snow", "The Bastard of Winterfell", "The Black Bastard of the Wall"),
                father = "",
                mother = "",
                spouse = "",
                allegiances = listOf("House Stark", "Night's Watch"),
                books = listOf("A Game of Thrones", "A Clash of Kings"),
                povBooks = listOf("A Dance with Dragons"),
                tvSeries =
                    listOf(
                        "Season 1",
                        "Season 2",
                        "Season 3",
                        "Season 4",
                        "Season 5",
                        "Season 6",
                        "Season 7",
                        "Season 8",
                    ),
                playedBy = listOf("Kit Harington"),
            ),
            Character(
                url = "https://www.anapioficeandfire.com/api/characters/148",
                name = "Tyrion Lannister",
                gender = "Male",
                culture = "Westermen",
                born = "In 273 AC, at Casterly Rock",
                died = "",
                titles = listOf("Hand of the Queen", "Lord of Casterly Rock"),
                aliases = listOf("The Imp", "Halfman", "The Little Lion"),
                father = "Tywin Lannister",
                mother = "Joanna Lannister",
                spouse = "Sansa Stark",
                allegiances = listOf("House Lannister", "House Targaryen"),
                books = listOf("A Game of Thrones", "A Clash of Kings"),
                povBooks = listOf("A Storm of Swords", "A Dance with Dragons"),
                tvSeries =
                    listOf(
                        "Season 1",
                        "Season 2",
                        "Season 3",
                        "Season 4",
                        "Season 5",
                        "Season 6",
                        "Season 7",
                        "Season 8",
                    ),
                playedBy = listOf("Peter Dinklage"),
            ),
            Character(
                url = "https://www.anapioficeandfire.com/api/characters/238",
                name = "Daenerys Targaryen",
                gender = "Female",
                culture = "Valyrian",
                born = "In 284 AC, at Dragonstone",
                died = "",
                titles =
                    listOf(
                        "Queen of the Andals, the Rhoynar, and the First Men",
                        "Protector of the Realm",
                        "Mother of Dragons",
                    ),
                aliases = listOf("Dany", "Daenerys Stormborn", "The Unburnt", "Mother of Dragons", "Khaleesi"),
                father = "Aerys II Targaryen",
                mother = "Rhaella Targaryen",
                spouse = "Khal Drogo",
                allegiances = listOf("House Targaryen"),
                books = listOf("A Game of Thrones"),
                povBooks = listOf("A Game of Thrones", "A Clash of Kings", "A Storm of Swords", "A Dance with Dragons"),
                tvSeries =
                    listOf(
                        "Season 1",
                        "Season 2",
                        "Season 3",
                        "Season 4",
                        "Season 5",
                        "Season 6",
                        "Season 7",
                        "Season 8",
                    ),
                playedBy = listOf("Emilia Clarke"),
            ),
            Character(
                url = "https://www.anapioficeandfire.com/api/characters/339",
                name = "Arya Stark",
                gender = "Female",
                culture = "Northmen",
                born = "In 289 AC, at Winterfell",
                died = "",
                titles = listOf(),
                aliases = listOf("Arry", "Cat of the Canals", "No One"),
                father = "Eddard Stark",
                mother = "Catelyn Tully",
                spouse = "",
                allegiances = listOf("House Stark"),
                books = listOf("A Game of Thrones", "A Clash of Kings"),
                povBooks =
                    listOf(
                        "A Game of Thrones",
                        "A Clash of Kings",
                        "A Storm of Swords",
                        "A Feast for Crows",
                        "A Dance with Dragons",
                    ),
                tvSeries =
                    listOf(
                        "Season 1",
                        "Season 2",
                        "Season 3",
                        "Season 4",
                        "Season 5",
                        "Season 6",
                        "Season 7",
                        "Season 8",
                    ),
                playedBy = listOf("Maisie Williams"),
            ),
            Character(
                url = "https://www.anapioficeandfire.com/api/characters/901",
                name = "Cersei Lannister",
                gender = "Female",
                culture = "Westermen",
                born = "In 266 AC, at Casterly Rock",
                died = "",
                titles = listOf("Queen of the Seven Kingdoms", "Queen Regent"),
                aliases = listOf("Light of the West"),
                father = "Tywin Lannister",
                mother = "Joanna Lannister",
                spouse = "Robert Baratheon",
                allegiances = listOf("House Lannister", "House Baratheon of King's Landing"),
                books = listOf("A Game of Thrones", "A Clash of Kings", "A Storm of Swords"),
                povBooks = listOf("A Feast for Crows", "A Dance with Dragons"),
                tvSeries =
                    listOf(
                        "Season 1",
                        "Season 2",
                        "Season 3",
                        "Season 4",
                        "Season 5",
                        "Season 6",
                        "Season 7",
                        "Season 8",
                    ),
                playedBy = listOf("Lena Headey"),
            ),
        )

    override suspend fun refreshCharacters() = Unit

    override suspend fun loadMore(): List<Character> = emptyList()

    override fun hasMore(): Boolean = false
}
