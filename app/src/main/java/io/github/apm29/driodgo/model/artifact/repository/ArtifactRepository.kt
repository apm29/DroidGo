package io.github.apm29.driodgo.model.artifact.repository

import androidx.lifecycle.MutableLiveData
import io.github.apm29.core.arch.IOSensitive
import io.github.apm29.core.utils.autoThreadSwitch
import io.github.apm29.core.utils.subscribeAuto
import io.github.apm29.driodgo.model.artifact.bean.CardListItem
import io.github.apm29.driodgo.model.artifact.db.ArtifactCardDao
import io.github.apm29.driodgo.model.artifact.db.CardEntity
import io.github.apm29.core.utils.Event
import javax.inject.Inject
import io.reactivex.Single

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
            .doOnError {
                io.message.value = Event(it.message.toString())
                io.loading.value = Event(false)
            }
            .subscribeAuto { artifact ->
                Single.create<List<Long>> {
                    it.onSuccess(artifactCardDao.insertAll(
                        artifact.cardSet.cardList.map { item ->
                            CardEntity(cardListItem = item)
                        }
                    ))
                }.autoThreadSwitch()
                    .doOnError {
                        io.message.value = Event(it.message.toString())
                        io.loading.value = Event(false)
                    }
                    .doOnSuccess {
                        io.loading.value = Event(false)
                    }
                    .subscribeAuto {
                        artifactItems.value = artifact.cardSet.cardList
                    }

            }
    }

    private fun loadCardFromDb(
        artifactItems: MutableLiveData<List<CardListItem>>,
        io: IOSensitive,
        reload: Boolean = false,
        onNoData: () -> Unit
    ) {
        artifactCardDao.getCardAll()
            .autoThreadSwitch()
            .doOnSubscribe {
                io.loading.value = Event(true)
            }
            .doOnError {
                io.message.value = Event(it.message.toString())
                io.loading.value = Event(false)
            }
            .subscribeAuto {
                if (it.isNotEmpty() && !reload) {
                    artifactItems.value = it.map {
                        it.cardListItem
                    }
                    io.loading.value = Event(false)
                } else {
                    onNoData()
                }
            }
    }

    fun loadCard(artifactItems: MutableLiveData<List<CardListItem>>, io: IOSensitive, reload: Boolean = false) {
        loadCardFromDb(
            artifactItems, io, reload
        ) {
            doFetchArtifact(artifactItems, io)
        }
    }
}