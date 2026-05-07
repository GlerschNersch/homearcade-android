package com.homearcade.android.di

import com.homearcade.android.data.api.HomeArcadeApi
import com.homearcade.android.data.local.AppPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * OkHttpClient with a dynamic base-URL interceptor that reads the
     * server address from DataStore on every request, so URL changes
     * take effect immediately without restarting the app.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(prefs: AppPreferences): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                // Attach HA token if configured
                val token = runBlocking { prefs.haToken.first() }
                val request = if (token.isNotBlank()) {
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else {
                    chain.request()
                }
                chain.proceed(request)
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            )
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, prefs: AppPreferences): Retrofit {
        // Read saved server URL (falls back to localhost for dev)
        val baseUrl = runBlocking {
            prefs.serverUrl.first().ifBlank { "http://localhost:5000" }
        }.let { if (it.endsWith("/")) it else "$it/" }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): HomeArcadeApi =
        retrofit.create(HomeArcadeApi::class.java)
}
