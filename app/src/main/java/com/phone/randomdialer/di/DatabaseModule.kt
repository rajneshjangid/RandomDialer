package com.phone.randomdialer.di

import android.content.Context
import androidx.room.Room
import com.phone.randomdialer.Utils.Constant.APP_DATABASE
import com.phone.randomdialer.data.local.AppDatabase
import com.phone.randomdialer.data.local.CallDataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            APP_DATABASE
        ).build()
    }

    @Singleton
    @Provides
    fun provideCallDao(appDatabase: AppDatabase): CallDataDao {
        return appDatabase.callDao()
    }
}