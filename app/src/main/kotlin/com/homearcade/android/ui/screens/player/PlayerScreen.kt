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
import com.homearcade.android.emulation.EmulationManager
import com.swordfish.libretrodroid.GLRetroView
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    romId: Int,
    onBack: () -> Unit,
    vm: PlayerViewModel = hiltViewModel(),
    emulationManager: EmulationManager = hiltViewModel<PlayerViewModel>()
        .let { androidx.hilt.navigation.compose.hiltViewModel() }
        .let { emulationManager },
) {
    LaunchedEffect(romId) { vm.loadRom(romId) }
    val state by vm.state.collectAsState()

    Box(Modifier.fillMaxSize()) {
        when (val s = state) {
            is PlayerState.Idle, is PlayerState.Downloading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
                Text(
                    if (s is PlayerState.Downloading) "Downloading ROM…" else "Loading…",
                    Modifier.align(Alignment.Center).padding(top = 64.dp),
                )
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
                // Render the libretro GLSurface
                val viewData = remember(s.romFile, s.systemId) {
                    emulationManager.buildViewData(s.systemId, s.romFile)
                }
                if (viewData != null) {
                    AndroidView(
                        factory = { ctx -> GLRetroView(ctx, viewData) },
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Text(
                        "No emulation core available for this system.",
                        Modifier.align(Alignment.Center),
                    )
                }
                // Back button overlay
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }
            }
        }
    }
}

// Workaround to inject EmulationManager into a Composable
private val PlayerScreen.emulationManager: EmulationManager get() = error("use Hilt")
