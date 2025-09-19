package com.tillylabs.gameofthroneswiki.ui.preview

import androidx.compose.runtime.Composable
import com.tillylabs.gameofthroneswiki.di.AppModule
import org.koin.compose.KoinApplicationPreview
import org.koin.ksp.generated.module

@Composable
fun GoTPreview(content: @Composable () -> Unit) {
    KoinApplicationPreview(application = { modules(AppModule().module) }) {
        content()
    }
}
