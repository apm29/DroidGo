package io.github.apm29.driodgo.model.artifact.bean

import androidx.room.ColumnInfo

data class CardReference(
    @ColumnInfo
    val card_id: Int?,
    @ColumnInfo
    val ref_type:String?,
    @ColumnInfo
    val count :Int?,
    @ColumnInfo
    var image:String?
)