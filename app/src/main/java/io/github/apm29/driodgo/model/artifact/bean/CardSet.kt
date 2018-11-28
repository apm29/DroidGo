package io.github.apm29.driodgo.model.artifact.bean

import com.google.gson.annotations.SerializedName

data class CardSet(@SerializedName("set_info")
                   val setInfo: SetInfo?,
                   @SerializedName("card_list")
                   val cardList: List<CardListItem>,
                   @SerializedName("version")
                   val version: Int?)