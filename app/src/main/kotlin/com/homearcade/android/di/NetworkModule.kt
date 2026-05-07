package com.homearcade.android.di

import com.homearcade.android.data.api.HomeArcadeApi
import com.homearcade.android.data.api.IngressAuthInterceptor
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
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideIngressAuthInterceptor(prefs: AppPreferences): IngressAuthInterceptor =
        IngressAuthInterceptor(prefs)

    @Provides
    @Singleton
    fun provideOkHttpClient(ingressAuth: IngressAuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)  // ROM downloads can be slow
            .addInterceptor(ingressAuth)        // handles both Bearer + ingress cookie
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            )
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, prefs: AppPreferences): Retrofit {
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
