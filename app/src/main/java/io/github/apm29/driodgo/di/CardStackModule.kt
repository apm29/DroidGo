package io.github.apm29.driodgo.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import io.github.apm29.core.arch.IOSensitive
import io.github.apm29.driodgo.anno.FragmentScope
import io.github.apm29.driodgo.model.artifact.bean.CardListItem
import io.github.apm29.driodgo.model.artifact.repository.ArtifactRepository
import io.github.apm29.driodgo.model.artifact.db.ArtifactCardDao
import io.github.apm29.driodgo.model.artifact.db.ArtifactCardDataBase
import io.github.apm29.driodgo.ui.home.CardAdapter
import io.github.apm29.driodgo.ui.home.CardStackFragment
import io.github.apm29.driodgo.vm.CardStackViewModel
import javax.inject.Inject

@Module(
    includes = [
        ArtifactApiModule::class
    ]
)
class CardStackModule(private val cardStackFragment: CardStackFragment) {
    @Provides
    @FragmentScope
    fun provideHomeViewModel(factory: CardStackViewModelFactory): CardStackViewModel {
        println("CardStackModule.provideHomeViewModel")
        return ViewModelProviders.of(cardStackFragment,factory).get(CardStackViewModel::class.java)
    }

    @Provides
    @FragmentScope
    fun provideIO(): IOSensitive {
        return cardStackFragment.io
    }



    @Provides
    @FragmentScope
    fun provideCardAdapter(initData:MutableList<CardListItem>):CardAdapter{
        return CardAdapter(cardStackFragment.requireContext(),initData)
    }

    @Provides
    @FragmentScope
    fun provideInitData():MutableList<CardListItem>{
        return mutableListOf()
    }


    class CardStackViewModelFactory @Inject constructor(
        private  val artifactRepository: ArtifactRepository, private val io: IOSensitive
    ):ViewModelProvider.Factory{
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass == CardStackViewModel::class.java){
                return CardStackViewModel(
                    artifactRepository,io
                ) as T
            }else{
                throw IllegalArgumentException("class must be ${CardStackViewModel::class.java.canonicalName}")
            }
        }
    }


    @Provides
    @FragmentScope
    fun providesArtifactDao():ArtifactCardDao{
        return ArtifactCardDataBase.getInstance(cardStackFragment.requireContext()).getArtifactCardDao()
    }
}