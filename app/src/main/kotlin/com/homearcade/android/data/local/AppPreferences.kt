package com.homearcade.android.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "homearcade_prefs")

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        private val SERVER_URL = stringPreferencesKey("server_url")
        private val HA_TOKEN   = stringPreferencesKey("ha_token")
    }

    val serverUrl: Flow<String> = context.dataStore.data.map { it[SERVER_URL] ?: "" }
    val haToken: Flow<String>   = context.dataStore.data.map { it[HA_TOKEN]   ?: "" }

    suspend fun setServerUrl(url: String) {
        context.dataStore.edit { it[SERVER_URL] = url.trimEnd('/') }
    }

    suspend fun setHaToken(token: String) {
        context.dataStore.edit { it[HA_TOKEN] = token }
    }

    /** True when a server URL has been saved (setup complete). */
    val isConfigured: Flow<Boolean> = serverUrl.map { it.isNotBlank() }
}
