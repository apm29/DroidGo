package io.github.apm29.driodgo.model.artifact.db

import androidx.room.Embedded
import androidx.room.Entity
import io.github.apm29.driodgo.model.artifact.bean.CardListItem


@Entity(tableName = "artifact_card",primaryKeys = [
    "card_id"
])
data class CardEntity(

    @Embedded
    val cardListItem: CardListItem
)

