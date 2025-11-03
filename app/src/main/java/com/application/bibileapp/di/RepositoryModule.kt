package com.application.bibileapp.di


import com.application.bibileapp.data.network.BibleApiService
import com.application.bibileapp.data.repository.BibleRepository
import com.application.bibileapp.data.repository.BibleRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideBibleRepository(api: BibleApiService): BibleRepository {
        return BibleRepositoryImpl(api)
    }
}
