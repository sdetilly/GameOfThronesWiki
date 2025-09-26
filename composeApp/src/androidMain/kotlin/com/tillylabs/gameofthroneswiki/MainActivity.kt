package com.tillylabs.gameofthroneswiki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.tillylabs.gameofthroneswiki.di.AppModule
import com.tillylabs.gameofthroneswiki.ui.MainScreen
import com.tillylabs.gameofthroneswiki.ui.theme.GameOfThronesTheme
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication
import org.koin.ksp.generated.module

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            KoinApplication(application = {
                androidContext(this@MainActivity)
                modules(AppModule().module, getPlatformModule())
            }) {
                GameOfThronesTheme {
                    MainScreen()
                }
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
