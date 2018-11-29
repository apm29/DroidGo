package io.github.apm29.core.arch.dagger

import android.app.Application
import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.Component
import io.github.apm29.core.arch.DroidGoApp
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * 核心模块
 * 提供:application层面单例的内容
 */
@Singleton
@Component(
    modules = [
        RemoteRepositoryModule::class,
        SharedPreferencesModule::class
    ]
)
interface CoreComponent {
    fun inject(app: DroidGoApp)

    fun okHttp(): OkHttpClient

    fun retrofit(): Retrofit

    fun gson(): Gson

    fun shared(): SharedPreferences

    fun application(): Application


    @Component.Builder
    interface Builder {

        fun build(): CoreComponent

        fun app(appModule: AppModule): Builder

        fun remote(remoteRepositoryModule: RemoteRepositoryModule = RemoteRepositoryModule()): Builder

        fun shared(sharedPreferences: SharedPreferencesModule = SharedPreferencesModule()): Builder

    }

}