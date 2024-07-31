package com.doachgosum.inapppurchasedemo.di

import javax.inject.Qualifier

class DispatcherQualifiers {
    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class DefaultDispatcher

    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class IoDispatcher
}