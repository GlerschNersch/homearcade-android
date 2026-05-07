package com.homearcade.android.emulation

import android.content.Context
import com.swordfish.libretrodroid.GLRetroView
import com.swordfish.libretrodroid.GLRetroViewData
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages lifecycle of the LibretroDroid [GLRetroView].
 *
 * Responsibilities:
 *  - Resolve core .so path from assets
 *  - Load ROM file from the local cache
 *  - Expose save-state read/write helpers
 *  - Forward controller input events
 */
@Singleton
class EmulationManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    /** Directory where downloaded ROM files are cached locally. */
    val romCacheDir: File
        get() = File(context.cacheDir, "roms").also { it.mkdirs() }

    /** Directory where save state files are stored locally. */
    val saveStateDir: File
        get() = File(context.filesDir, "saves").also { it.mkdirs() }

    /**
     * Build the [GLRetroViewData] needed to construct a [GLRetroView].
     * The caller (PlayerScreen) is responsible for composing the view into
     * the Compose hierarchy via AndroidView.
     */
    fun buildViewData(systemId: String, romFile: File): GLRetroViewData? {
        val coreName = CoreMapper.coreForSystem(systemId) ?: return null
        val coreFile = File(context.applicationInfo.nativeLibraryDir, coreName)
        if (!coreFile.exists()) return null

        return GLRetroViewData(context).apply {
            coreFilePath = coreFile.absolutePath
            gameFilePath = romFile.absolutePath
            shader        = GLRetroView.SHADER_DEFAULT
        }
    }

    fun saveStatePath(romId: Int, slot: Int): File =
        File(saveStateDir, "rom_${romId}_slot_$slot.state")

    fun cachedRomPath(romId: Int, filename: String): File =
        File(romCacheDir, "${romId}_$filename")
}
