package com.tillylabs.gameofthroneswiki.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.tillylabs.gameofthroneswiki.ui.books.BookDetailsScreen
import com.tillylabs.gameofthroneswiki.ui.books.BooksScreen
import com.tillylabs.gameofthroneswiki.ui.characters.CharactersScreen
import com.tillylabs.gameofthroneswiki.ui.houses.HousesScreen
import com.tillylabs.gameofthroneswiki.ui.navigation.BookDetails
import com.tillylabs.gameofthroneswiki.ui.navigation.Books
import com.tillylabs.gameofthroneswiki.ui.navigation.Characters
import com.tillylabs.gameofthroneswiki.ui.navigation.Houses

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Books,
            modifier = Modifier.fillMaxSize(),
        ) {
            bottomTabComposable<Books> { backStackEntry ->
                BooksScreen(
                    onBookClick = { bookUrl ->
                        navController.navigate(BookDetails(bookUrl))
                    },
                    onNavigateToCharacters = {
                        navController.navigate(Characters) {
                            popUpTo(Books) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToHouses = {
                        navController.navigate(Houses) {
                            popUpTo(Books) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    viewModelStoreOwner = backStackEntry,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@bottomTabComposable,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            composable<BookDetails> { backStackEntry ->
                val bookDetails = backStackEntry.toRoute<BookDetails>()
                BookDetailsScreen(
                    bookUrl = bookDetails.bookUrl,
                    onNavigateBack = { navController.navigateUp() },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            bottomTabComposable<Characters> { backStackEntry ->
                CharactersScreen(
                    onNavigateToBooks = {
                        navController.navigate(Books) {
                            popUpTo(Books) {
                                inclusive = false
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToHouses = {
                        navController.navigate(Houses) {
                            popUpTo(Books) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    viewModelStoreOwner = backStackEntry,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            bottomTabComposable<Houses> { backStackEntry ->
                HousesScreen(
                    onNavigateToBooks = {
                        navController.navigate(Books) {
                            popUpTo(Books) {
                                inclusive = false
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToCharacters = {
                        navController.navigate(Characters) {
                            popUpTo(Books) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    viewModelStoreOwner = backStackEntry,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

private inline fun <reified T : Any> NavGraphBuilder.bottomTabComposable(
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable<T>(
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
        content = content,
    )
}
