package com.homearcade.android.ui.navigation

import androidx.lifecycle.ViewModel
import com.homearcade.android.data.local.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavViewModel @Inject constructor(val prefs: AppPreferences) : ViewModel()
