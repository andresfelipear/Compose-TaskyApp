package com.aarevalo.tasky.auth.di

import com.aarevalo.tasky.BuildConfig
import com.aarevalo.tasky.agenda.data.remote.api.TaskyAgendaApi
import com.aarevalo.tasky.auth.data.util.InputValidatorImpl
import com.aarevalo.tasky.auth.domain.util.InputValidator
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
    fun provideAuthenticatedApi(
        moshi: Moshi,
        @Named("unauthenticated")client: OkHttpClient
    ): TaskyAgendaApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
            .create()
    }

    @Singleton
    @Provides
    fun provideInputValidator(): InputValidator {
        return InputValidatorImpl()
    }
}