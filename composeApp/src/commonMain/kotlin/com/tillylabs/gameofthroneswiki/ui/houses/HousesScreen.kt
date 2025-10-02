package com.tillylabs.gameofthroneswiki.ui.houses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.tillylabs.gameofthroneswiki.ui.preview.GoTPreview
import com.tillylabs.gameofthroneswiki.usecase.HousesUseCasePreview
import com.tillylabs.gameofthroneswiki.usecase.PreviewState
import gameofthroneswiki.composeapp.generated.resources.Res
import gameofthroneswiki.composeapp.generated.resources.ic_book
import gameofthroneswiki.composeapp.generated.resources.ic_castle
import gameofthroneswiki.composeapp.generated.resources.ic_person
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HousesScreen(
    onNavigateToBooks: () -> Unit = {},
    onNavigateToCharacters: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current),
    viewModel: HousesViewModel = koinViewModel(viewModelStoreOwner = viewModelStoreOwner),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToBooks,
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
                    selected = true,
                    onClick = { },
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
                    if (uiState.houses.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No houses to display",
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding =
                                androidx.compose.foundation.layout
                                    .PaddingValues(16.dp),
                        ) {
                            items(uiState.houses) { house ->
                                HouseItem(house = house)
                            }

                            if (uiState.hasMoreData) {
                                item {
                                    if (uiState.isLoadingMore) {
                                        Box(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    } else {
                                        // Trigger load more when this item becomes visible
                                        androidx.compose.runtime.LaunchedEffect(Unit) {
                                            viewModel.loadMoreHouses()
                                        }
                                        Box(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
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
private fun HouseItem(
    house: com.tillylabs.gameofthroneswiki.models.House,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = house.name,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Preview
@Composable
private fun HousesScreenDataPreview() {
    GoTPreview {
        HousesScreen(
            viewModel = HousesViewModel(HousesUseCasePreview()),
        )
    }
}

@Preview
@Composable
private fun HousesScreenLoadingPreview() {
    GoTPreview {
        HousesScreen(
            viewModel = HousesViewModel(HousesUseCasePreview(PreviewState.LOADING)),
        )
    }
}

@Preview
@Composable
private fun HousesScreenErrorPreview() {
    GoTPreview {
        HousesScreen(
            viewModel = HousesViewModel(HousesUseCasePreview(PreviewState.ERROR)),
        )
    }
}

@Preview
@Composable
private fun HousesScreenEmptyPreview() {
    GoTPreview {
        HousesScreen(
            viewModel = HousesViewModel(HousesUseCasePreview(PreviewState.EMPTY)),
        )
    }
}
