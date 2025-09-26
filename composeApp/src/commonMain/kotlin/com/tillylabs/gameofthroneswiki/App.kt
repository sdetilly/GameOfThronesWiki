package com.tillylabs.gameofthroneswiki

import androidx.compose.runtime.Composable
import com.tillylabs.gameofthroneswiki.di.AppModule
import com.tillylabs.gameofthroneswiki.ui.MainScreen
import com.tillylabs.gameofthroneswiki.ui.theme.GameOfThronesTheme
import org.koin.compose.KoinApplication
import org.koin.ksp.generated.module

@Composable
fun App() {
    KoinApplication(application = {
        modules(AppModule().module, getPlatformModule())
    }) {
        GameOfThronesTheme {
            MainScreen()
        }
    }
}

expect fun getPlatformModule(): org.koin.core.module.Module
