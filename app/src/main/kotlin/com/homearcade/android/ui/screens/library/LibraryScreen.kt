package com.homearcade.android.ui.screens.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.homearcade.android.data.api.model.Rom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    systemId: String,
    onRomClick: (Int) -> Unit,
    onBack: () -> Unit,
    vm: LibraryViewModel = hiltViewModel(),
) {
    LaunchedEffect(systemId) { vm.load(systemId) }

    val roms    by vm.roms.collectAsState()
    val loading by vm.isLoading.collectAsState()
    val error   by vm.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(systemId.uppercase()) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                error != null -> Text(error ?: "", Modifier.align(Alignment.Center))
                else -> LazyVerticalGrid(
                    columns = GridCells.Adaptive(140.dp),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(roms) { rom -> RomCard(rom, onClick = { onRomClick(rom.id) }) }
                }
            }
        }
    }
}

@Composable
private fun RomCard(rom: Rom, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column {
            if (rom.artUrl != null) {
                AsyncImage(
                    model = rom.artUrl,
                    contentDescription = rom.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.75f)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    Modifier.fillMaxWidth().aspectRatio(0.75f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("🎮", style = MaterialTheme.typography.headlineLarge)
                }
            }
            Text(
                text = rom.title,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(8.dp),
                maxLines = 2,
            )
        }
    }
}
