package com.homearcade.android.di

import com.homearcade.android.data.repository.RomRepository
import com.homearcade.android.data.repository.SaveStateRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// Repositories are @Singleton and @Inject-constructor — Hilt binds them automatically.
// This module is a placeholder for future interface-to-impl bindings.
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule
