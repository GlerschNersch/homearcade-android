package com.homearcade.android.data.api.model

data class RomSystem(
    val id: String,          // e.g. "ps1", "snes", "n64"
    val name: String,
    val romCount: Int,
    val coverUrl: String? = null,
)
