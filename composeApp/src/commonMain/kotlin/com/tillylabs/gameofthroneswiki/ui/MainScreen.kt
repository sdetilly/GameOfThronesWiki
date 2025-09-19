package com.tillylabs.gameofthroneswiki.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.tillylabs.gameofthroneswiki.ui.books.BooksScreen
import com.tillylabs.gameofthroneswiki.ui.characters.CharactersScreen
import com.tillylabs.gameofthroneswiki.ui.houses.HousesScreen
import com.tillylabs.gameofthroneswiki.ui.navigation.NavigationDestination
import gameofthroneswiki.composeapp.generated.resources.Res
import gameofthroneswiki.composeapp.generated.resources.ic_book
import gameofthroneswiki.composeapp.generated.resources.ic_castle
import gameofthroneswiki.composeapp.generated.resources.ic_person
import org.jetbrains.compose.resources.vectorResource

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(NavigationDestination.BOOKS) }

    val tabs =
        listOf(
            TabItem(
                title = "Books",
                icon = vectorResource(Res.drawable.ic_book),
                destination = NavigationDestination.BOOKS,
            ),
            TabItem(
                title = "Characters",
                icon = vectorResource(Res.drawable.ic_person),
                destination = NavigationDestination.CHARACTERS,
            ),
            TabItem(
                title = "Houses",
                icon = vectorResource(Res.drawable.ic_castle),
                destination = NavigationDestination.HOUSES,
            ),
        )

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab.destination,
                        onClick = { selectedTab = tab.destination },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                            )
                        },
                        label = { Text(tab.title) },
                    )
                }
            }
        },
    ) { paddingValues ->
        when (selectedTab) {
            NavigationDestination.BOOKS -> BooksScreen(modifier = Modifier.fillMaxSize().padding(paddingValues))
            NavigationDestination.CHARACTERS -> CharactersScreen(modifier = Modifier.fillMaxSize().padding(paddingValues))
            NavigationDestination.HOUSES -> HousesScreen(modifier = Modifier.fillMaxSize().padding(paddingValues))
        }
    }
}

private data class TabItem(
    val title: String,
    val icon: ImageVector,
    val destination: NavigationDestination,
)
