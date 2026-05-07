package com.homearcade.android.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homearcade.android.data.api.HomeArcadeApi
import com.homearcade.android.data.api.model.Rom
import com.homearcade.android.emulation.EmulationManager
import com.swordfish.libretrodroid.GLRetroViewData
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
    data class  Ready(val viewData: GLRetroViewData) : PlayerState()
    data class  Error(val message: String)           : PlayerState()
}

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val api: HomeArcadeApi,
    private val emulationManager: EmulationManager,
) : ViewModel() {

    private val _state = MutableStateFlow<PlayerState>(PlayerState.Idle)
    val state = _state.asStateFlow()

    fun loadRom(romId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = PlayerState.Downloading
            try {
                val rom = api.getRom(romId)
                val cached = emulationManager.cachedRomPath(rom.id, rom.filename)

                if (!cached.exists()) {
                    val response = api.downloadRom(rom.id)
                    if (!response.isSuccessful) {
                        _state.value = PlayerState.Error("Download failed (${response.code()})")
                        return@launch
                    }
                    response.body()?.byteStream()?.use { input ->
                        cached.outputStream().use { output -> input.copyTo(output) }
                    }
                }

                val viewData = emulationManager.buildViewData(rom.systemId, cached)
                    ?: run {
                        _state.value = PlayerState.Error("No emulation core for '${rom.systemId}'")
                        return@launch
                    }

                _state.value = PlayerState.Ready(viewData)
            } catch (e: Exception) {
                _state.value = PlayerState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
