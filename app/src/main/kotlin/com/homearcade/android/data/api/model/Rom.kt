package com.homearcade.android.data.api.model

data class Rom(
    val id: Int,
    val title: String,
    val systemId: String,
    val filename: String,
    val coverUrl: String? = null,
    val description: String? = null,
    val genre: String? = null,
    val releaseYear: Int? = null,
    val developer: String? = null,
    val rating: Float? = null,
    val playTimeMinutes: Int = 0,
    val lastPlayedAt: Long? = null,
    val playStatus: String? = null,   // "backlog" | "playing" | "completed" | "dropped"
    val isNew: Boolean = false,
)
