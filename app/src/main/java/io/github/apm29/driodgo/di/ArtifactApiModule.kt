package io.github.apm29.driodgo.di

import dagger.Module
import dagger.Provides
import io.github.apm29.driodgo.anno.ArtifactAffair
import io.github.apm29.driodgo.anno.FragmentScope
import io.github.apm29.driodgo.model.artifact.api.ArtifactService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class ArtifactApiModule {
    @Provides
    @FragmentScope
    fun providesArtifactService(@ArtifactAffair retrofit: Retrofit): ArtifactService {
        println("ArtifactApiModule.providesArtifactService")
        return retrofit.create(ArtifactService::class.java)
    }

    @Provides
    @FragmentScope
    @ArtifactAffair
    fun provideArtifactRetrofit(okHttpClient: OkHttpClient.Builder):Retrofit{
        println("ArtifactApiModule.provideArtifactRetrofit")
        return Retrofit.Builder()
            .baseUrl("https://playartifact.com")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()
    }



}