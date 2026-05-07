package com.homearcade.android.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homearcade.android.data.api.model.Rom
import com.homearcade.android.data.repository.RomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val romRepository: RomRepository,
) : ViewModel() {

    private val _roms = MutableStateFlow<List<Rom>>(emptyList())
    val roms = _roms.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun load(systemId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            romRepository.getRoms(systemId)
                .onSuccess { _roms.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }
}
