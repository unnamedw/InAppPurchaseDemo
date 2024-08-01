package com.doachgosum.inapppurchasedemo.di

import android.content.Context
import com.doachgosum.inapppurchasedemo.utils.BillingClientWrapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesBillingClientWrapper(
        @ApplicationContext context: Context,
        @DispatcherQualifiers.IoDispatcher ioDispatcher: CoroutineDispatcher
    ): BillingClientWrapper = BillingClientWrapper(context, ioDispatcher)

    @Provides
    @Named(DiConstant.Named.PACKAGE_NAME)
    fun providePackageName(@ApplicationContext applicationContext: Context) = applicationContext.packageName
}