package com.homearcade.android.data.api

import com.homearcade.android.data.api.model.IntegrationSettings
import com.homearcade.android.data.api.model.Rom
import com.homearcade.android.data.api.model.RomSystem
import com.homearcade.android.data.api.model.SaveSlot
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit interface that mirrors the HomeArcade server REST API.
 * Base URL is set dynamically from AppPreferences (the user's server address).
 */
interface HomeArcadeApi {

    // ── Systems ──────────────────────────────────────────────────
    @GET("api/systems")
    suspend fun getSystems(): List<RomSystem>

    // ── ROMs ─────────────────────────────────────────────────────
    @GET("api/roms")
    suspend fun getRoms(@Query("systemId") systemId: String? = null): List<Rom>

    @GET("api/roms/{id}")
    suspend fun getRom(@Path("id") id: Int): Rom

    /** Download the ROM file itself for local emulation. */
    @Streaming
    @GET("api/roms/{id}/download")
    suspend fun downloadRom(@Path("id") id: Int): Response<ResponseBody>

    // ── Save states ───────────────────────────────────────────────
    @GET("api/roms/{id}/save-slots")
    suspend fun getSaveSlots(@Path("id") romId: Int): List<SaveSlot>

    @POST("api/roms/{id}/save-slots")
    suspend fun upsertSaveSlot(
        @Path("id") romId: Int,
        @Body slot: SaveSlot,
    ): SaveSlot

    @DELETE("api/roms/{id}/save-slots/{slot}")
    suspend fun deleteSaveSlot(
        @Path("id") romId: Int,
        @Path("slot") slot: Int,
    ): Response<Unit>

    /** Upload a save state file to server backup. */
    @Streaming
    @PUT("api/roms/{id}/save-backup/{slot}")
    suspend fun uploadSaveBackup(
        @Path("id") romId: Int,
        @Path("slot") slot: Int,
        @Body body: okhttp3.RequestBody,
    ): Response<Unit>

    /** Download a save state file from server backup. */
    @Streaming
    @GET("api/roms/{id}/save-backup/{slot}")
    suspend fun downloadSaveBackup(
        @Path("id") romId: Int,
        @Path("slot") slot: Int,
    ): Response<ResponseBody>

    // ── Settings ──────────────────────────────────────────────────
    @GET("api/settings/integration")
    suspend fun getIntegrationSettings(): IntegrationSettings

    @PUT("api/settings/integration")
    suspend fun saveIntegrationSettings(@Body settings: IntegrationSettings): IntegrationSettings
}
