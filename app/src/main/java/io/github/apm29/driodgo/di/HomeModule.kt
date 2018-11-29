package io.github.apm29.driodgo.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import io.github.apm29.core.arch.IOSensitive
import io.github.apm29.core.arch.IOSensitiveViewModel
import io.github.apm29.driodgo.ui.home.MainActivity
import io.github.apm29.driodgo.anno.ActivityScope
import io.github.apm29.driodgo.model.artifact.repository.ArtifactRepository
import io.github.apm29.driodgo.model.artifact.db.ArtifactCardDao
import io.github.apm29.driodgo.model.artifact.db.ArtifactCardDataBase
import io.github.apm29.driodgo.vm.HomeViewModel
import javax.inject.Inject

@Module(
    includes = [
        ArtifactApiModule::class
    ]
)
class HomeModule(private val activity: MainActivity) {
    @Provides
    @ActivityScope
    fun provideHomeViewModel(factory: HomeViewModelFactory): HomeViewModel {
        println("HomeModule.provideHomeViewModel")
        return ViewModelProviders.of(activity,factory).get(HomeViewModel::class.java)
    }

    @Provides
    @ActivityScope
    fun provideIO(): IOSensitive {
        return activity.io
    }


    class HomeViewModelFactory @Inject constructor(
        private  val artifactRepository: ArtifactRepository, private val io: IOSensitive
    ):ViewModelProvider.Factory{
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass == HomeViewModel::class.java){
                return HomeViewModel(
                    artifactRepository,io
                ) as T
            }else{
                throw IllegalArgumentException("class must be ${HomeViewModel::class.java.canonicalName}")
            }
        }
    }


    @Provides
    @ActivityScope
    fun providesArtifactDao():ArtifactCardDao{
        return ArtifactCardDataBase.getInstance(activity).getArtifactCardDao()
    }
}