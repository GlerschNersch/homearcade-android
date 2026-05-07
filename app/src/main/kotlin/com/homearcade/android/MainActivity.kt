package com.homearcade.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.homearcade.android.ui.navigation.HomeArcadeNavGraph
import com.homearcade.android.ui.theme.HomeArcadeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeArcadeTheme {
                HomeArcadeNavGraph()
            }
        }
    }
}
