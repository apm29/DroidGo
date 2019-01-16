package io.github.apm29.driodgo.vm

import androidx.lifecycle.*
import io.github.apm29.core.arch.IOSensitive
import io.github.apm29.driodgo.model.artifact.bean.CardListItem
import io.github.apm29.driodgo.model.artifact.repository.ArtifactRepository
import javax.inject.Inject

class CardDetailViewModel @Inject constructor(
    private val artifactRepository: ArtifactRepository, private val io: IOSensitive
) : ViewModel() {

    val cardDetailData: MutableLiveData<CardListItem> = MutableLiveData()

    fun getCardDetail(id: Int) {
        artifactRepository.loadCardDetail(id, cardDetailData, io)
    }
}