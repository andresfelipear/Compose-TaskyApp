package com.aarevalo.tasky.agenda.di

import com.aarevalo.tasky.BuildConfig
import com.aarevalo.tasky.agenda.data.remote.api.TaskyAgendaApi
import com.aarevalo.tasky.agenda.data.repository.OfflineFirstAgendaRepository
import com.aarevalo.tasky.agenda.domain.AgendaRepository
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
object TaskyAgendaModule {

    @Provides
    @Singleton
    fun provideAgendaApi(
        moshi: Moshi,
        @Named("authenticated")client: OkHttpClient
    ): TaskyAgendaApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideAgendaRepository(
        api: TaskyAgendaApi,
        sessionStorage: SessionStorage
    ) : AgendaRepository {
        return OfflineFirstAgendaRepository(api, sessionStorage)
    }


}