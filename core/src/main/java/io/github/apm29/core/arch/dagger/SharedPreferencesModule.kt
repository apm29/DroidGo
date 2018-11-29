package io.github.apm29.core.arch.dagger

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * 基础SP配置
 */
@Module(
    includes = [
        AppModule::class
    ]
)
class SharedPreferencesModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences("stored_sp_data", Context.MODE_PRIVATE)
    }
}