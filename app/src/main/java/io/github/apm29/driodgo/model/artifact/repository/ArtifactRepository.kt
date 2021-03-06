package io.github.apm29.driodgo.model.artifact.repository

import androidx.lifecycle.MutableLiveData
import io.github.apm29.core.arch.IOSensitive
import io.github.apm29.core.utils.Event
import io.github.apm29.core.utils.autoThreadSwitch
import io.github.apm29.core.utils.subscribeAuto
import io.github.apm29.driodgo.model.artifact.api.ArtifactService
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

    /**
     * 获取CardSet url -> 获取CardSet -> 将结果存入room -> 整理room数据 -> 返回card列表
     */
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
            .subscribeAuto(io,"更新数据库..") {
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
            .subscribeAuto(io,"获取Artifact数据..") {
                if (it.isNotEmpty() && !reload) {
                    artifactItems.value = it.map {
                        it.cardListItem
                    }
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

    fun loadCardDetail(id:Int,cardDetailData: MutableLiveData<CardListItem>, io: IOSensitive) {
        artifactCardDao.getCardById(id)
            .autoThreadSwitch()
            .subscribeAuto(io,"加载卡牌详情.."){
                cardDetailData.value = it.cardListItem
            }
    }

}