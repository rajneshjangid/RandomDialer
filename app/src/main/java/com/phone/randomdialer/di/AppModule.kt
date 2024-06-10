package com.phone.randomdialer.di

import android.content.Context
import com.phone.randomdialer.data.local.CallDataDao
import com.phone.randomdialer.data.remote.ApiService
import com.phone.randomdialer.data.repository.CallRepositoryImpl
import com.phone.randomdialer.domain.repository.CallRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCallRepository(
        apiService: ApiService,
        callDataDao: CallDataDao,
        @ApplicationContext context: Context
    ): CallRepository {
        return CallRepositoryImpl(
            apiService = apiService,
            callDataDao = callDataDao,
            context = context
        )
    }
}