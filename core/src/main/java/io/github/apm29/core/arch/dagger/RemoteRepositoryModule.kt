package io.github.apm29.core.arch.dagger

import com.google.gson.Gson
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import dagger.Module
import dagger.Provides
import io.github.apm29.core.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * 基础network配置
 */
@Module(
    includes = [AppModule::class]
)
class RemoteRepositoryModule {

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient.Builder, gson: Gson): Retrofit.Builder {
        Timber.d("retrofit created")
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client.build())
    }

    @Singleton
    @Provides
    fun provideClient(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(
                //日志LOG
                LoggingInterceptor.Builder()
                    .loggable(BuildConfig.DEBUG)
                    .setLevel(Level.BASIC)
                    .log(Platform.INFO)
                    .request("Request-LOG")
                    .response("Response-LOG")
                    .addHeader("version", BuildConfig.VERSION_NAME)
                    .build()
            )
    }
}