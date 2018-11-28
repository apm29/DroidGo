package io.github.apm29.driodgo.vm

import androidx.lifecycle.MutableLiveData
import io.github.apm29.core.arch.IOSensitiveViewModel
import io.github.apm29.driodgo.model.artifact.bean.CardListItem
import io.github.apm29.driodgo.model.artifact.repository.ArtifactRepository
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val artifactRepository: ArtifactRepository
):IOSensitiveViewModel() {


    val artifactItems:MutableLiveData<List<CardListItem>> = MutableLiveData()


    fun loadArtifact(reload:Boolean = false){
        artifactRepository.loadCard(
            artifactItems,this,reload
        )
    }

}