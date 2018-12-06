package io.github.apm29.driodgo.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.apm29.core.arch.IOSensitive
import io.github.apm29.driodgo.model.artifact.bean.CardListItem
import io.github.apm29.driodgo.model.artifact.bean.MiniImage
import io.github.apm29.driodgo.model.artifact.repository.ArtifactRepository
import io.reactivex.Single
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val artifactRepository: ArtifactRepository,val io: IOSensitive
):ViewModel() {

    val artifactItems:MutableLiveData<List<CardListItem>> = MutableLiveData()


    fun loadArtifact(reload:Boolean = false){
        artifactRepository.loadCard(
            artifactItems,io,reload
        )
    }

}