package com.phone.randomdialer.di

import android.content.Context
import android.content.SharedPreferences
import com.phone.randomdialer.Utils.Constant.APP_PREF
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class PreferenceModule {
    @Singleton
    @Provides
    fun providePreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            APP_PREF,
            Context.MODE_PRIVATE
        )
    }
}