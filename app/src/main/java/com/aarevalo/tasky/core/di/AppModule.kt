package com.aarevalo.tasky.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import com.aarevalo.tasky.core.data.auth.AuthenticatedUserSerializable
import com.aarevalo.tasky.core.data.preferences.EncryptAuthenticatedUser
import com.aarevalo.tasky.core.data.preferences.SessionStorageSerializer
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val Context.userDataStore by dataStore(
        fileName = "user_preferences.json",
        serializer = EncryptAuthenticatedUser,
        corruptionHandler = ReplaceFileCorruptionHandler(
            produceNewData = { AuthenticatedUserSerializable() }
        )
    )

    @Singleton
    @Provides
    fun provideUserDataStore(
        @ApplicationContext context: Context
    ): DataStore<AuthenticatedUserSerializable> {
        return context.userDataStore
    }

    @Singleton
    @Provides
    fun provideSessionStorage(
        userDataStore: DataStore<AuthenticatedUserSerializable>
    ): SessionStorage {
        return SessionStorageSerializer(userDataStore)
    }
}