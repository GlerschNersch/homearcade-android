package com.homearcade.android.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homearcade.android.data.api.model.RomSystem
import com.homearcade.android.data.repository.RomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val romRepository: RomRepository,
) : ViewModel() {

    private val _systems = MutableStateFlow<List<RomSystem>>(emptyList())
    val systems = _systems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            romRepository.getSystems()
                .onSuccess { _systems.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }
}
