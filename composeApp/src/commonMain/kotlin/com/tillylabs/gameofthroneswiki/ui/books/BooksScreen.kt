package com.tillylabs.gameofthroneswiki.ui.books

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.tillylabs.gameofthroneswiki.models.BookWithCover
import com.tillylabs.gameofthroneswiki.ui.preview.GoTPreview
import com.tillylabs.gameofthroneswiki.usecase.BooksUseCasePreview
import com.tillylabs.gameofthroneswiki.usecase.PreviewState
import gameofthroneswiki.composeapp.generated.resources.Res
import gameofthroneswiki.composeapp.generated.resources.ic_book
import gameofthroneswiki.composeapp.generated.resources.ic_castle
import gameofthroneswiki.composeapp.generated.resources.ic_person
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BooksScreen(
    onBookClick: (String) -> Unit = {},
    onNavigateToCharacters: () -> Unit = {},
    onNavigateToHouses: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: BooksViewModel = koinViewModel(),
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = {
                        Icon(
                            imageVector = vectorResource(Res.drawable.ic_book),
                            contentDescription = "Books",
                        )
                    },
                    label = { Text("Books") },
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToCharacters,
                    icon = {
                        Icon(
                            imageVector = vectorResource(Res.drawable.ic_person),
                            contentDescription = "Characters",
                        )
                    },
                    label = { Text("Characters") },
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHouses,
                    icon = {
                        Icon(
                            imageVector = vectorResource(Res.drawable.ic_castle),
                            contentDescription = "Houses",
                        )
                    },
                    label = { Text("Houses") },
                )
            }
        },
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
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
                                text = uiState.error.orEmpty(),
                                color = MaterialTheme.colorScheme.error,
                            )
                            Button(onClick = { viewModel.retry() }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                else -> {
                    if (uiState.books.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No books to display",
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(16.dp),
                        ) {
                            items(uiState.books) { book ->
                                if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                                    with(sharedTransitionScope) {
                                        BookItem(
                                            book = book,
                                            onClick = { onBookClick(book.url) },
                                            animatedVisibilityScope = animatedVisibilityScope,
                                        )
                                    }
                                } else {
                                    BookItemNoSharedTransition(
                                        book = book,
                                        onClick = { onBookClick(book.url) },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookItemNoSharedTransition(
    book: BookWithCover,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AsyncImage(
                model = book.coverImageUrl,
                contentDescription = "${book.name} cover",
                modifier =
                    Modifier
                        .width(60.dp)
                        .height(90.dp)
                        .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = book.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                if (book.authors.isNotEmpty()) {
                    Text(
                        text = "by ${book.authors.joinToString(", ")}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = "${book.numberOfPages} pages",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.BookItem(
    book: BookWithCover,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    Card(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AsyncImage(
                model = book.coverImageUrl,
                contentDescription = "${book.name} cover",
                modifier =
                    Modifier
                        .width(60.dp)
                        .height(90.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .sharedElement(
                            rememberSharedContentState(key = "book-cover-${book.url}"),
                            animatedVisibilityScope,
                        ),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = book.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                if (book.authors.isNotEmpty()) {
                    Text(
                        text = "by ${book.authors.joinToString(", ")}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = "${book.numberOfPages} pages",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun BookScreenDataPreview() {
    GoTPreview {
        BooksScreen(
            onBookClick = {},
            viewModel = BooksViewModel(BooksUseCasePreview()),
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun BookScreenLoadingPreview() {
    GoTPreview {
        BooksScreen(
            onBookClick = {},
            viewModel = BooksViewModel(BooksUseCasePreview(PreviewState.LOADING)),
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun BookScreenErrorPreview() {
    GoTPreview {
        BooksScreen(
            onBookClick = {},
            viewModel = BooksViewModel(BooksUseCasePreview(PreviewState.ERROR)),
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun BookScreenEmptyPreview() {
    GoTPreview {
        BooksScreen(
            onBookClick = {},
            viewModel = BooksViewModel(BooksUseCasePreview(PreviewState.EMPTY)),
        )
    }
}
