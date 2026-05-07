package com.homearcade.android.data.repository

import com.homearcade.android.data.api.HomeArcadeApi
import com.homearcade.android.data.api.model.SaveSlot
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveStateRepository @Inject constructor(
    private val api: HomeArcadeApi,
) {
    suspend fun getSlots(romId: Int): Result<List<SaveSlot>> =
        runCatching { api.getSaveSlots(romId) }

    suspend fun upsertSlot(romId: Int, slot: SaveSlot): Result<SaveSlot> =
        runCatching { api.upsertSaveSlot(romId, slot) }

    suspend fun deleteSlot(romId: Int, slot: Int): Result<Unit> =
        runCatching { api.deleteSaveSlot(romId, slot).let { } }
}
