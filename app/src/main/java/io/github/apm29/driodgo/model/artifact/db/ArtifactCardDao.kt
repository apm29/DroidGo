package io.github.apm29.driodgo.model.artifact.db

import androidx.annotation.MainThread
import androidx.room.*
import io.github.apm29.driodgo.model.artifact.bean.CardReference
import io.github.apm29.driodgo.model.artifact.bean.MiniImage
import io.reactivex.Single


@Dao
abstract class ArtifactCardDao {

    @Query("Select * from artifact_card where card_type = :type")
    abstract fun getCardByType(type: String): Single<List<CardEntity>>

    @Query("Select * from artifact_card ")
    abstract fun getCardAll(): Single<List<CardEntity>>

    @Query("Select * from artifact_card where  card_id = :id")
    abstract fun getCardById(id:Int): Single<CardEntity>

    @Query("Select * from artifact_card ")
    abstract fun getCardAllSync(): List<CardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(cards: List<CardEntity>): List<Long>


    @Query("Select mini_image from artifact_card where card_id = :id ")
    abstract fun getCardRefImage(id: Int): Single<MiniImage>

    @Query("Select mini_image from artifact_card where card_id = :id ")
    abstract fun getCardRefImageSync(id: Int): MiniImage?

    @Query("Update artifact_card set 'references' = :refList where card_id = :id ")
    abstract fun updateCardReferencesSync(refList: List<CardReference>, id: Int)

    @Query("Update artifact_card set is_included = :isIncluded where card_id = :id ")
    abstract fun updateCardIncludedSync(isIncluded: Boolean, id: Int)

    fun updateAfter(): Single<List<CardEntity>> {
        return Single.create<List<CardEntity>> {
            it.onSuccess(doUpdate())
        }
    }

    /**
     * 1.给每个reference添加引用的MiniImage
     * 2.将被引用的卡标记
     */
    private fun doUpdate(): List<CardEntity> {
        val includesSet: MutableSet<Int> = mutableSetOf()
        val cardList = getCardAllSync()
        cardList
            .forEach { card ->
                val refList = card.cardListItem.references?.map { ref ->
                    if (ref.card_id != null) {
                        val miniImage = getCardRefImageSync(ref.card_id)
                        ref.image = miniImage?.default
                    }
                    ref
                }

                /* hero -->
                            Spell          -->
                                                1.Hero
                                                2.Creep
                            Passive Ability-->
                                                1.Hero
                                                2.Creep
                            Active Ability -->
                                                1.Hero
                                                2.Creep
                 */
                //标记已经被include的
                refList?.forEach {
                    if (it.card_id != null
                        && (it.ref_type != "references"
                                || (it.ref_type == "references"
                                && (card.cardListItem.card_type == "Passive Ability"|| card.cardListItem.card_type == "Ability"|| card.cardListItem.card_type == "Spell")))
                    ) {
                        includesSet.add(it.card_id)
                    }
                }



                if (card.cardListItem.card_id != null && refList != null)
                    updateCardReferencesSync(refList, card.cardListItem.card_id)

            }
        //修改被包含的,included = true
        cardList.forEach {
            val cardId = it.cardListItem.card_id
            if (cardId != null) {
                if (includesSet.contains(cardId)) {
                    updateCardIncludedSync(true, cardId)
                } else {
                    updateCardIncludedSync(false, cardId)
                }
            }
        }

        return getCardAllSync()
    }


}