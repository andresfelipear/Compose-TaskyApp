package com.aarevalo.tasky.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.aarevalo.tasky.core.data.preferences.EncryptTokenPreferences
import com.aarevalo.tasky.core.data.preferences.SerializeUserPreferences
import com.aarevalo.tasky.core.data.preferences.TokenPreferencesSerializer
import com.aarevalo.tasky.core.data.preferences.UserPreferencesSerializer
import com.aarevalo.tasky.core.domain.preferences.TokenPreferences
import com.aarevalo.tasky.core.domain.preferences.TokenPreferencesData
import com.aarevalo.tasky.core.domain.preferences.UserPreferences
import com.aarevalo.tasky.core.domain.preferences.UserPreferencesData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val Context.tokenDataStore by dataStore(
        fileName = "token_preferences.json",
        serializer = TokenPreferencesSerializer,
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { TokenPreferencesData() }
        )
    )

    private val Context.userDataStore by dataStore(
        fileName = "user_preferences.json",
        serializer = UserPreferencesSerializer,
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { UserPreferencesData() }
        )
    )

    @Singleton
    @Provides
    fun provideTokenDataStore(
        @ApplicationContext context: Context
    ): DataStore<TokenPreferencesData> {
        return context.tokenDataStore
    }

    @Singleton
    @Provides
    fun provideUserPreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<UserPreferencesData> {
        return context.userDataStore
    }

    @Singleton
    @Provides
    fun provideTokenPreferences(
        tokenDataStore: DataStore<TokenPreferencesData>
    ): TokenPreferences {
        return EncryptTokenPreferences(tokenDataStore)
    }

    @Singleton
    @Provides
    fun provideUserPreferences(
        userDataStore: DataStore<UserPreferencesData>
    ): UserPreferences {
        return SerializeUserPreferences(userDataStore)
    }


}