package io.github.apm29.core.arch.dagger

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * 核心配置
 */
@Module
class AppModule(private val application: Application) {

    @Provides
    fun provideApplication(): Application {
        return application
    }

    @Provides
    fun provideContext(): Context = application

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            .serializeNulls()
            .enableComplexMapKeySerialization()
            .create()
    }
}