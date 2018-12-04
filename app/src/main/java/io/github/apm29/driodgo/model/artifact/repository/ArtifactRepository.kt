package io.github.apm29.driodgo.model.artifact.repository

import androidx.lifecycle.MutableLiveData
import io.github.apm29.core.arch.IOSensitive
import io.github.apm29.core.utils.Event
import io.github.apm29.core.utils.autoThreadSwitch
import io.github.apm29.core.utils.subscribeAuto
import io.github.apm29.driodgo.model.artifact.bean.CardListItem
import io.github.apm29.driodgo.model.artifact.bean.MiniImage
import io.github.apm29.driodgo.model.artifact.db.ArtifactCardDao
import io.github.apm29.driodgo.model.artifact.db.CardEntity
import io.reactivex.Single
import javax.inject.Inject

class ArtifactRepository @Inject constructor(
    private val artifactService: ArtifactService,
    private val artifactCardDao: ArtifactCardDao
) {
    private fun doFetchArtifact(
        artifactItems: MutableLiveData<List<CardListItem>>,
        io: IOSensitive
    ) {
        artifactService
            .getArtifactUri()
            .autoThreadSwitch()
            .flatMap {
                artifactService
                    .getCardSet(it.cdnRoot + it.url)
                    .autoThreadSwitch()
            }
            .flatMap { artifact ->
                Single.create<List<Long>> {
                    it.onSuccess(artifactCardDao.insertAll(
                        artifact.cardSet.cardList.map { item ->
                            CardEntity(cardListItem = item)
                        }
                    ))
                }.autoThreadSwitch()
            }
            .flatMap {
                artifactCardDao.updateAfter()
                    .autoThreadSwitch()
            }
            .subscribeAuto(io) {
                artifactItems.value = it.map {
                    it.cardListItem
                }
            }
    }

    private fun loadCardFromDb(
        artifactItems: MutableLiveData<List<CardListItem>>,
        io: IOSensitive,
        reload: Boolean = false
    ) {
        artifactCardDao.getCardAll()
            .autoThreadSwitch()
            .subscribeAuto(io) {
                if (it.isNotEmpty() && !reload) {
                    artifactItems.value = it.map {
                        it.cardListItem
                    }
                    io.loading.value = Event(false)
                } else {
                    doFetchArtifact(artifactItems, io)
                }
            }
    }

    fun loadCard(artifactItems: MutableLiveData<List<CardListItem>>, io: IOSensitive, reload: Boolean = false) {
        loadCardFromDb(
            artifactItems, io, reload
        )
    }

    fun getImage(id: Int): Single<MiniImage> {
        return artifactCardDao.getCardRefImage(id)
    }
}