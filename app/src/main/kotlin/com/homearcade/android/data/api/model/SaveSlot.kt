package com.homearcade.android.data.api.model

data class SaveSlot(
    val id: Int,
    val romId: Int,
    val slot: Int,
    val label: String,
    val updatedAt: Long,
    val screenshotUrl: String? = null,
)
