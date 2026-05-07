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
    suspend fun getSystems(): Result<List<RomSystem>> = runCatching { api.getSystems() }

    suspend fun getRoms(systemId: String? = null): Result<List<Rom>> =
        runCatching { api.getRoms(systemId) }

    suspend fun getRom(id: Int): Result<Rom> = runCatching { api.getRom(id) }
}
