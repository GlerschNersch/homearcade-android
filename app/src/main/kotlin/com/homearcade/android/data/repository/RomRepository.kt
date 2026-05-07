package com.homearcade.android.data.repository

import com.homearcade.android.data.api.HomeArcadeApi
import com.homearcade.android.data.api.model.Rom
import com.homearcade.android.data.api.model.RomSystem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RomRepository @Inject constructor(
    private val api: HomeArcadeApi,
) {
    /**
     * Derives the list of systems from the full ROM list.
     * The server has no /api/systems endpoint; systems are implicit in each ROM's `system` field.
     */
    suspend fun getSystems(): Result<List<RomSystem>> = runCatching {
        val roms = api.getRoms()
        roms
            .groupBy { it.system }
            .map { (systemId, systemRoms) ->
                RomSystem(
                    id = systemId,
                    name = systemDisplayName(systemId),
                    romCount = systemRoms.size,
                    coverUrl = systemRoms.firstOrNull { it.artUrl != null }?.artUrl,
                )
            }
            .sortedBy { it.name }
    }

    suspend fun getRoms(systemId: String? = null): Result<List<Rom>> = runCatching {
        val all = api.getRoms()
        if (systemId != null) all.filter { it.system == systemId } else all
    }

    suspend fun getRom(id: Int): Result<Rom> = runCatching { api.getRom(id) }

    /** Maps internal system IDs to human-readable display names. */
    private fun systemDisplayName(id: String): String = when (id) {
        "nes"       -> "NES"
        "snes"      -> "Super Nintendo"
        "n64"       -> "Nintendo 64"
        "gba"       -> "Game Boy Advance"
        "gb"        -> "Game Boy"
        "gbc"       -> "Game Boy Color"
        "nds"       -> "Nintendo DS"
        "genesis"   -> "Sega Genesis"
        "ps1"       -> "PlayStation"
        "ps2"       -> "PlayStation 2"
        "psp"       -> "PlayStation Portable"
        "dreamcast" -> "Dreamcast"
        "arcade"    -> "Arcade"
        else        -> id.replaceFirstChar { it.uppercase() }
    }
}
