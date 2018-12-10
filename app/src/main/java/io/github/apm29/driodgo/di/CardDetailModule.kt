package io.github.apm29.driodgo.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import io.github.apm29.core.arch.IOSensitive
import io.github.apm29.driodgo.anno.FragmentScope
import io.github.apm29.driodgo.model.artifact.db.ArtifactCardDao
import io.github.apm29.driodgo.model.artifact.db.ArtifactCardDataBase
import io.github.apm29.driodgo.model.artifact.repository.ArtifactRepository
import io.github.apm29.driodgo.ui.home.CardDetailActivity
import io.github.apm29.driodgo.vm.CardDetailViewModel
import javax.inject.Inject

@Module(
    includes = [
        ArtifactApiModule::class
    ]
)
class CardDetailModule(private val cardDetailActivity: CardDetailActivity) {
    @Provides
    @FragmentScope
    fun provideCardDetailViewModel(factory: CardDetailViewModelFactory): CardDetailViewModel {
        println("CardStackModule.provideHomeViewModel")
        return ViewModelProviders.of(cardDetailActivity,factory).get(CardDetailViewModel::class.java)
    }

    @Provides
    @FragmentScope
    fun provideIO(): IOSensitive {
        return cardDetailActivity.ioSensitive
    }


    class CardDetailViewModelFactory @Inject constructor(
        private  val artifactRepository: ArtifactRepository, private val io: IOSensitive
    ):ViewModelProvider.Factory{
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass == CardDetailViewModel::class.java){
                return CardDetailViewModel(
                    artifactRepository,io
                ) as T
            }else{
                throw IllegalArgumentException("class must be ${CardDetailViewModel::class.java.canonicalName}")
            }
        }
    }


    @Provides
    @FragmentScope
    fun providesArtifactDao():ArtifactCardDao{
        return ArtifactCardDataBase.getInstance(cardDetailActivity).getArtifactCardDao()
    }
}