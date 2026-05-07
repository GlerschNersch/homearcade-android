package com.homearcade.android.data.api.model

import com.google.gson.annotations.SerializedName

/**
 * Mirrors the UploadedRom type returned by the HomeArcade server.
 * Field names match the camelCase JSON the server emits (drizzle ORM
 * auto-converts snake_case DB columns to camelCase).
 */
data class Rom(
    val id: Int,
    val title: String,
    /** The system ID, e.g. "ps1", "snes", "n64". */
    val system: String,
    val slug: String,
    val originalName: String = "",
    val fileName: String = "",
    val artUrl: String? = null,
    val description: String? = null,
    val genre: String? = null,
    val releaseYear: Int? = null,
    val developer: String? = null,
    val publisher: String? = null,
    val rating: Int = 0,
    val lastPlayed: Long = 0,
    val playCount: Int = 0,
    val minutesPlayed: Int = 0,
    val playStatus: String = "unset",
    val createdAt: Long = 0,
    val favorite: Boolean = true,
    val communityScore: Int? = null,
    val wheelArtUrl: String? = null,
)
