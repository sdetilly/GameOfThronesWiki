package com.tillylabs.gameofthroneswiki.ui.characters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tillylabs.gameofthroneswiki.models.Character
import com.tillylabs.gameofthroneswiki.models.CharacterWithBooks
import gameofthroneswiki.composeapp.generated.resources.Res
import gameofthroneswiki.composeapp.generated.resources.ic_arrow_back
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailsScreen(
    characterUrl: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CharacterDetailsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(characterUrl) {
        viewModel.loadCharacterDetails(characterUrl)
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        TopAppBar(
            title = { Text("Character Details") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_arrow_back),
                        contentDescription = "Back",
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(),
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = uiState.error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                        )
                        Button(onClick = { viewModel.loadCharacterDetails(characterUrl) }) {
                            Text("Retry")
                        }
                    }
                }
            }

            else -> {
                uiState.characterWithBooks?.let { characterWithBooks ->
                    CharacterDetailsContent(
                        characterWithBooks = characterWithBooks,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun CharacterDetailsContent(
    characterWithBooks: CharacterWithBooks,
    modifier: Modifier = Modifier,
) {
    val character = characterWithBooks.character
    Column(
        modifier =
            modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text =
                        character.name.ifEmpty {
                            "Unknown Character"
                        },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )

                if (character.aliases.isNotEmpty()) {
                    Text(
                        text = "Also known as: ${character.aliases.joinToString(", ")}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        PersonalDetailsCard(character)

        if (character.father.isNotBlank() ||
            character.mother.isNotBlank() ||
            character.spouse.isNotBlank()
        ) {
            FamilyDetailsCard(character)
        }

        if (character.titles.isNotEmpty() || character.allegiances.isNotEmpty()) {
            TitlesAndAllegiancesCard(character)
        }

        MediaAppearancesCard(character, characterWithBooks.books, characterWithBooks.povBooks)
    }
}

@Composable
private fun PersonalDetailsCard(character: Character) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Personal Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            if (character.gender.isNotBlank()) {
                DetailRow("Gender", character.gender)
            }
            if (character.culture.isNotBlank()) {
                DetailRow("Culture", character.culture)
            }
            if (character.born.isNotBlank()) {
                DetailRow("Born", character.born)
            }
            if (character.died.isNotBlank()) {
                DetailRow("Died", character.died)
            }
        }
    }
}

@Composable
private fun FamilyDetailsCard(character: Character) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Family",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            if (character.father.isNotBlank()) {
                DetailRow("Father", extractNameFromUrl(character.father))
            }
            if (character.mother.isNotBlank()) {
                DetailRow("Mother", extractNameFromUrl(character.mother))
            }
            if (character.spouse.isNotBlank()) {
                DetailRow("Spouse", extractNameFromUrl(character.spouse))
            }
        }
    }
}

@Composable
private fun TitlesAndAllegiancesCard(character: Character) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Titles & Allegiances",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            if (character.titles.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Titles",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    character.titles.forEach { title ->
                        Text(
                            text = "• $title",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }

            if (character.allegiances.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Allegiances",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    character.allegiances.forEach { allegiance ->
                        Text(
                            text = "• ${extractNameFromUrl(allegiance)}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaAppearancesCard(
    character: Character,
    books: List<com.tillylabs.gameofthroneswiki.models.BookWithCover>,
    povBooks: List<com.tillylabs.gameofthroneswiki.models.BookWithCover>,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Appearances",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            if (books.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Books",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    books.forEach { book ->
                        Text(
                            text = "• ${book.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
            if (povBooks.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "POV Books",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    povBooks.forEach { book ->
                        Text(
                            text = "• ${book.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
            if (character.tvSeries.isNotEmpty() &&
                character.tvSeries.any { it.isNotBlank() }
            ) {
                DetailRow("TV Series", character.tvSeries.filter { it.isNotBlank() }.joinToString(", "))
            }
            if (character.playedBy.isNotEmpty() &&
                character.playedBy.any { it.isNotBlank() }
            ) {
                DetailRow("Played By", character.playedBy.filter { it.isNotBlank() }.joinToString(", "))
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
    }
}

private fun extractNameFromUrl(url: String): String =
    if (url.isBlank()) {
        "Unknown"
    } else {
        url.split("/").lastOrNull() ?: "Unknown"
    }
