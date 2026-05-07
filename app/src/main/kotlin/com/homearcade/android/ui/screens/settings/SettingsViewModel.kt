package com.homearcade.android.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homearcade.android.data.local.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: AppPreferences,
) : ViewModel() {

    var serverUrl by mutableStateOf("")
    var haToken   by mutableStateOf("")
    var saved     by mutableStateOf(false)

    init {
        viewModelScope.launch {
            serverUrl = prefs.serverUrl.first()
            haToken   = prefs.haToken.first()
        }
    }

    fun save() {
        viewModelScope.launch {
            prefs.setServerUrl(serverUrl)
            prefs.setHaToken(haToken)
            saved = true
        }
    }
}
