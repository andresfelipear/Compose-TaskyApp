package com.aarevalo.tasky.auth.di

import com.aarevalo.tasky.BuildConfig
import com.aarevalo.tasky.auth.data.remote.api.TaskyAuthApi
import com.aarevalo.tasky.auth.data.repository.AuthenticationRepositoryImpl
import com.aarevalo.tasky.auth.data.util.InputValidatorImpl
import com.aarevalo.tasky.auth.domain.repository.AuthenticationRepository
import com.aarevalo.tasky.auth.domain.util.InputValidator
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TaskyAuthenticationModule {

    @Provides
    @Singleton
    fun provideAuthenticationApi(
        moshi: Moshi,
        @Named("unauthenticated")client: OkHttpClient
    ): TaskyAuthApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideAuthenticationRepository(
        api: TaskyAuthApi,
        sessionStorage: SessionStorage
    ) : AuthenticationRepository {
        return AuthenticationRepositoryImpl(api, sessionStorage)
    }

    @Singleton
    @Provides
    fun provideInputValidator(): InputValidator {
        return InputValidatorImpl()
    }
}