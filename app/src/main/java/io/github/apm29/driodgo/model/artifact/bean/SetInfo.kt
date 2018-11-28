package io.github.apm29.driodgo.model.artifact.bean

import com.google.gson.annotations.SerializedName

data class SetInfo(@SerializedName("pack_item_def")
                   val packItemDef: Int?,
                   @SerializedName("name")
                   val name: Name?,
                   @SerializedName("set_id")
                   val setId: Int?)