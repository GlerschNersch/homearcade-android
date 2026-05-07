package com.homearcade.android.ui.screens.setup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homearcade.android.data.local.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val prefs: AppPreferences,
) : ViewModel() {

    var serverUrl by mutableStateOf("")
    var haToken   by mutableStateOf("")

    private val _saving = MutableStateFlow(false)
    val saving = _saving.asStateFlow()

    fun save(onComplete: () -> Unit) {
        viewModelScope.launch {
            _saving.value = true
            prefs.setServerUrl(serverUrl)
            prefs.setHaToken(haToken)
            _saving.value = false
            onComplete()
        }
    }
}
