package com.homearcade.android.ui.screens.player

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.swordfish.libretrodroid.GLRetroView

@Composable
fun PlayerScreen(
    romId: Int,
    onBack: () -> Unit,
    vm: PlayerViewModel = hiltViewModel(),
) {
    LaunchedEffect(romId) { vm.loadRom(romId) }
    val state by vm.state.collectAsState()

    Box(Modifier.fillMaxSize()) {
        when (val s = state) {
            is PlayerState.Idle, is PlayerState.Downloading -> {
                Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text(if (s is PlayerState.Downloading) "Downloading ROM…" else "Loading…")
                }
            }
            is PlayerState.Error -> Column(
                Modifier.align(Alignment.Center).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Failed to load game", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(s.message, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(16.dp))
                Button(onClick = onBack) { Text("Go back") }
            }
            is PlayerState.Ready -> {
                AndroidView(
                    factory = { ctx -> GLRetroView(ctx, s.viewData) },
                    modifier = Modifier.fillMaxSize(),
                )
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, "Back",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}
