package com.homearcade.android.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homearcade.android.data.api.HomeArcadeApi
import com.homearcade.android.data.api.model.Rom
import com.homearcade.android.emulation.EmulationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

sealed class PlayerState {
    data object Idle        : PlayerState()
    data object Downloading : PlayerState()
    data class  Ready(val romFile: File, val systemId: String) : PlayerState()
    data class  Error(val message: String) : PlayerState()
}

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val api: HomeArcadeApi,
    private val emulationManager: EmulationManager,
) : ViewModel() {

    private val _state = MutableStateFlow<PlayerState>(PlayerState.Idle)
    val state = _state.asStateFlow()

    private var currentRom: Rom? = null

    fun loadRom(romId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = PlayerState.Downloading
            try {
                val rom = api.getRom(romId)
                currentRom = rom

                val cached = emulationManager.cachedRomPath(rom.id, rom.filename)
                if (!cached.exists()) {
                    val response = api.downloadRom(rom.id)
                    if (!response.isSuccessful) {
                        _state.value = PlayerState.Error("Download failed: ${response.code()}")
                        return@launch
                    }
                    response.body()?.byteStream()?.use { input ->
                        cached.outputStream().use { output -> input.copyTo(output) }
                    }
                }
                _state.value = PlayerState.Ready(cached, rom.systemId)
            } catch (e: Exception) {
                _state.value = PlayerState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
