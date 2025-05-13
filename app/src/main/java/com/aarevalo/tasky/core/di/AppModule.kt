package com.aarevalo.tasky.core.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.aarevalo.tasky.core.data.preferences.TokenPreferencesImp
import com.aarevalo.tasky.core.data.preferences.UserPreferencesImp
import com.aarevalo.tasky.core.domain.preferences.TokenPreferences
import com.aarevalo.tasky.core.domain.preferences.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(
        app: Application
    ): SharedPreferences {
        return app.getSharedPreferences("shared_pref", MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideTokenPreferences(
        sharedPreferences: SharedPreferences
    ) : TokenPreferences {
        return TokenPreferencesImp(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideUserPreferences(
        sharedPreferences: SharedPreferences
    ) : UserPreferences {
        return UserPreferencesImp(sharedPreferences)
    }
}