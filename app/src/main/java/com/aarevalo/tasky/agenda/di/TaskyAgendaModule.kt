package com.aarevalo.tasky.agenda.di

import android.content.Context
import androidx.room.Room
import com.aarevalo.tasky.BuildConfig
import com.aarevalo.tasky.agenda.data.local.RoomLocalAgendaDataSource
import com.aarevalo.tasky.agenda.data.local.dao.AttendeeDao
import com.aarevalo.tasky.agenda.data.local.dao.DeletedItemSyncDao
import com.aarevalo.tasky.agenda.data.local.dao.EventDao
import com.aarevalo.tasky.agenda.data.local.dao.PendingItemSyncDao
import com.aarevalo.tasky.agenda.data.local.dao.PhotoDao
import com.aarevalo.tasky.agenda.data.local.dao.ReminderDao
import com.aarevalo.tasky.agenda.data.local.dao.TaskDao
import com.aarevalo.tasky.agenda.data.local.database.AgendaDatabase
import com.aarevalo.tasky.agenda.data.remote.RetrofitRemoteAgendaDataSource
import com.aarevalo.tasky.agenda.data.remote.api.TaskyAgendaApi
import com.aarevalo.tasky.agenda.data.OfflineFirstAgendaRepository
import com.aarevalo.tasky.agenda.data.util.AndroidPhotoByteLoader
import com.aarevalo.tasky.agenda.data.util.StandardDispatcherProvider
import com.aarevalo.tasky.agenda.domain.AgendaRepository
import com.aarevalo.tasky.agenda.domain.LocalAgendaDataSource
import com.aarevalo.tasky.agenda.domain.RemoteAgendaDataSource
import com.aarevalo.tasky.agenda.domain.util.PhotoByteLoader
import com.aarevalo.tasky.core.domain.preferences.SessionStorage
import com.aarevalo.tasky.core.domain.util.DispatcherProvider
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    fun provideRemoteAgendaDataSource(
        api: TaskyAgendaApi,
        photoByteLoader: PhotoByteLoader
    ): RemoteAgendaDataSource {
        return RetrofitRemoteAgendaDataSource(
            api,
            photoByteLoader
        )
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AgendaDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AgendaDatabase::class.java,
            "agenda_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideEventDao(
        database: AgendaDatabase
    ): EventDao = database.eventDao

    @Provides
    @Singleton
    fun provideTaskDao(
        database: AgendaDatabase
    ): TaskDao = database.taskDao

    @Provides
    @Singleton
    fun provideReminderDao(
        database: AgendaDatabase
    ): ReminderDao = database.reminderDao

    @Provides
    @Singleton
    fun provideAttendeeDao(
        database: AgendaDatabase
    ): AttendeeDao = database.attendeeDao

    @Provides
    @Singleton
    fun providePhotoDao(
        database: AgendaDatabase
    ): PhotoDao = database.photoDao

    @Provides
    @Singleton
    fun provideDeletedItemSyncDao(
        database: AgendaDatabase
    ): DeletedItemSyncDao = database.deletedItemSyncDao

    @Provides
    @Singleton
    fun providePendingItemSyncDao(
        database: AgendaDatabase
    ): PendingItemSyncDao = database.pendingItemSyncDao

    @Provides
    @Singleton
    fun provideLocalAgendaDataSource(
        database: AgendaDatabase,
        eventDao: EventDao,
        taskDao: TaskDao,
        reminderDao: ReminderDao,
        attendeeDao: AttendeeDao,
        photoDao: PhotoDao
    ): LocalAgendaDataSource = RoomLocalAgendaDataSource(
        database,
        eventDao,
        taskDao,
        reminderDao,
        attendeeDao,
        photoDao
    )

    @Provides
    @Singleton
    fun provideAgendaRepository(
        remoteAgendaDataSource: RemoteAgendaDataSource,
        localAgendaSource: LocalAgendaDataSource,
        sessionStorage: SessionStorage,
        pendingItemSyncDao: PendingItemSyncDao,
        coroutineScope: CoroutineScope

    ) : AgendaRepository {
        return OfflineFirstAgendaRepository(
            remoteAgendaDataSource,
            localAgendaSource,
            sessionStorage,
            coroutineScope,
            pendingItemSyncDao
        )
    }

    @Provides
    @Singleton
    fun providePhotoByteLoader(
        @ApplicationContext context: Context,
        dispatcherProvider: DispatcherProvider
    ) : PhotoByteLoader {
        return AndroidPhotoByteLoader(context, dispatcherProvider)
    }

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider {
        return StandardDispatcherProvider
    }

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.IO)
    }
}