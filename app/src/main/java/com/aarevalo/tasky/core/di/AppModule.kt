package com.aarevalo.tasky.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import com.aarevalo.tasky.BuildConfig
import com.aarevalo.tasky.core.data.auth.AuthTokenInterceptor
import com.aarevalo.tasky.core.data.auth.AuthenticatedUserSerializable
import com.aarevalo.tasky.core.data.preferences.EncryptAuthenticatedUser
import com.aarevalo.tasky.core.data.preferences.SessionStorageSerializer
import com.aarevalo.tasky.core.data.remote.api.TaskyRefreshTokenApi
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Named
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

    @Provides
    @Singleton
    fun provideAuthTokenInterceptor(
        sessionStorage: SessionStorage,
        refreshTokenApi: TaskyRefreshTokenApi
    ): AuthTokenInterceptor {
        return AuthTokenInterceptor(sessionStorage, refreshTokenApi)
    }

    @Provides
    @Singleton
    @Named("unauthenticated")
    fun provideUnauthenticatedOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
    }

    @Provides
    @Singleton
    @Named("authenticated")
    fun provideOkHttpClient(authTokenInterceptor: AuthTokenInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .addInterceptor(authTokenInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi{
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideRefreshTokenApi(
        moshi: Moshi,
        @Named("authenticated") client: OkHttpClient
    ): TaskyRefreshTokenApi{
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
            .create()
    }
}