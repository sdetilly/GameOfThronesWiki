package com.tillylabs.gameofthroneswiki.ui.books

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.tillylabs.gameofthroneswiki.models.BookWithCover
import gameofthroneswiki.composeapp.generated.resources.Res
import gameofthroneswiki.composeapp.generated.resources.ic_arrow_back
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun BookDetailsScreen(
    bookUrl: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookDetailsViewModel = koinViewModel(),
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(bookUrl) {
        viewModel.loadBookDetails(bookUrl)
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        TopAppBar(
            title = { Text("Book Details") },
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
                        Button(onClick = { viewModel.loadBookDetails(bookUrl) }) {
                            Text("Retry")
                        }
                    }
                }
            }

            else -> {
                uiState.book?.let { book ->
                    if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                        with(sharedTransitionScope) {
                            BookDetailsContent(
                                book = book,
                                animatedVisibilityScope = animatedVisibilityScope,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    } else {
                        BookDetailsContentNoSharedTransition(
                            book = book,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BookDetailsContentNoSharedTransition(
    book: BookWithCover,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Book cover and basic info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                AsyncImage(
                    model = book.coverImageUrl,
                    contentDescription = "${book.name} cover",
                    modifier =
                        Modifier
                            .width(200.dp)
                            .aspectRatio(2f / 3f)
                            .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = book.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )

                    if (book.authors.isNotEmpty()) {
                        Text(
                            text = "by ${book.authors.joinToString(", ")}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        // Publication details
        PublicationDetailsCard(book)

        // Character counts
        if (book.characters.isNotEmpty() || book.povCharacters.isNotEmpty()) {
            CharacterCountsCard(book)
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.BookDetailsContent(
    book: BookWithCover,
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    Column(
        modifier =
            modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Book cover and basic info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                AsyncImage(
                    model = book.coverImageUrl,
                    contentDescription = "${book.name} cover",
                    modifier =
                        Modifier
                            .width(200.dp)
                            .aspectRatio(2f / 3f)
                            .sharedElement(
                                rememberSharedContentState(key = "book-cover-${book.url}"),
                                animatedVisibilityScope,
                            ).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = book.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )

                    if (book.authors.isNotEmpty()) {
                        Text(
                            text = "by ${book.authors.joinToString(", ")}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        // Publication details
        PublicationDetailsCard(book)

        // Character counts
        if (book.characters.isNotEmpty() || book.povCharacters.isNotEmpty()) {
            CharacterCountsCard(book)
        }
    }
}

@Composable
private fun PublicationDetailsCard(book: BookWithCover) {
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
                text = "Publication Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            DetailRow("Publisher", book.publisher)
            DetailRow("Country", book.country)
            DetailRow("Media Type", book.mediaType)
            DetailRow("Pages", book.numberOfPages.toString())
            if (book.isbn.isNotBlank()) {
                DetailRow("ISBN", book.isbn)
            }
            if (book.released.isNotBlank()) {
                DetailRow("Released", book.released.substringBefore("T"))
            }
        }
    }
}

@Composable
private fun CharacterCountsCard(book: BookWithCover) {
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
                text = "Characters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            if (book.characters.isNotEmpty()) {
                DetailRow("Total Characters", book.characters.size.toString())
            }
            if (book.povCharacters.isNotEmpty()) {
                DetailRow("POV Characters", book.povCharacters.size.toString())
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
