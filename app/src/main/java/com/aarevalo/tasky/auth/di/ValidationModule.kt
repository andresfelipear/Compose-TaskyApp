package com.aarevalo.tasky.auth.di

import com.aarevalo.tasky.auth.data.util.InputValidatorImpl
import com.aarevalo.tasky.auth.domain.util.InputValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ValidationModule {

    @Singleton
    @Provides
    fun provideInputValidator(): InputValidator {
        return InputValidatorImpl()
    }
}