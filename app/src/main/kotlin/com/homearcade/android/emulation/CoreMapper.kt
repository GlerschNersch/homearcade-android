package com.homearcade.android.emulation

/**
 * Maps HomeArcade system IDs to LibretroDroid core asset names.
 * Each core .so must be included in the app's assets/cores/ directory.
 */
object CoreMapper {
    private val CORES = mapOf(
        "nes"      to "fceumm_libretro_android.so",
        "snes"     to "snes9x_libretro_android.so",
        "n64"      to "mupen64plus_next_libretro_android.so",
        "gba"      to "mgba_libretro_android.so",
        "gb"       to "mgba_libretro_android.so",
        "gbc"      to "mgba_libretro_android.so",
        "genesis"  to "genesis_plus_gx_libretro_android.so",
        "ps1"      to "pcsx_rearmed_libretro_android.so",
        "ps2"      to "pcsx2_libretro_android.so",
        "psp"      to "ppsspp_libretro_android.so",
        "nds"      to "melonds_libretro_android.so",
        "dreamcast" to "flycast_libretro_android.so",
        "arcade"   to "mame2003_plus_libretro_android.so",
    )

    fun coreForSystem(systemId: String): String? = CORES[systemId]

    /** File extension(s) that each system's ROM files use. */
    val systemExtensions = mapOf(
        "nes"      to listOf("nes"),
        "snes"     to listOf("sfc", "smc"),
        "n64"      to listOf("n64", "z64", "v64"),
        "gba"      to listOf("gba"),
        "gb"       to listOf("gb"),
        "gbc"      to listOf("gbc"),
        "genesis"  to listOf("md", "gen", "smd"),
        "ps1"      to listOf("cue", "bin", "iso", "chd", "pbp"),
        "ps2"      to listOf("iso", "chd"),
        "psp"      to listOf("iso", "cso", "pbp"),
        "nds"      to listOf("nds"),
        "dreamcast" to listOf("gdi", "cdi", "chd"),
        "arcade"   to listOf("zip"),
    )
}
