package io.github.apm29.driodgo.model.artifact.bean

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.ContextCompat
import io.github.apm29.driodgo.R
import io.github.apm29.driodgo.model.artifact.CardType

data class CardListItem(
    val base_card_id: Int?,
    val card_name: CardName?,
    val card_text: CardText?,
    val large_image: LargeImage?,
    val ingame_image: InGameImage?,
    val card_type: String?,
    val card_id: Int?,
    val mini_image: MiniImage?,
    val references: List<CardReference>?,
    val illustrator: String?,
    val rarity: String?,

    val is_black: Boolean?,
    val is_red: Boolean?,
    val is_green: Boolean?,
    val is_blue: Boolean?,

    val is_crosslane: Boolean?,
    val charges: Int?,//能量
    val sub_type: String?,//饰品/护甲/武器
    val gold_cost: Int?,//金钱
    val mana_cost: Int?,//魔法消耗
    val attack: Int?,//攻击
    val armor: Int?,//护甲
    val hit_points: Int?,//生命

    var is_included: Boolean = false//是否是其他卡牌附带
) : Parcelable {
    fun getColor(context: Context): ColorStateList {
        if (isExpand){
            return ColorStateList.valueOf(Color.WHITE)
        }
        val color: Int = when {
            is_black == true -> ContextCompat.getColor(context, R.color.card_black)
            is_blue == true -> ContextCompat.getColor(context, R.color.card_blue)
            is_green == true -> ContextCompat.getColor(context, R.color.card_green)
            is_red == true -> ContextCompat.getColor(context, R.color.card_red)
            else -> ContextCompat.getColor(context, R.color.card_gold)
        }
        return ColorStateList.valueOf(color)
    }

    @Transient
    var isExpand: Boolean = true

    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readParcelable(CardName::class.java.classLoader),
        parcel.readParcelable(CardText::class.java.classLoader),
        parcel.readParcelable(LargeImage::class.java.classLoader),
        parcel.readParcelable(InGameImage::class.java.classLoader),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readParcelable(MiniImage::class.java.classLoader),
        parcel.createTypedArrayList(CardReference),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readByte() != 0.toByte()
    ) {
    }


    fun getType():CardType{
        return CardType.getType(this)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(base_card_id)
        parcel.writeParcelable(card_name, flags)
        parcel.writeParcelable(card_text, flags)
        parcel.writeParcelable(large_image, flags)
        parcel.writeParcelable(ingame_image, flags)
        parcel.writeString(card_type)
        parcel.writeValue(card_id)
        parcel.writeParcelable(mini_image, flags)
        parcel.writeTypedList(references)
        parcel.writeString(illustrator)
        parcel.writeString(rarity)
        parcel.writeValue(is_black)
        parcel.writeValue(is_red)
        parcel.writeValue(is_green)
        parcel.writeValue(is_blue)
        parcel.writeValue(is_crosslane)
        parcel.writeValue(charges)
        parcel.writeString(sub_type)
        parcel.writeValue(gold_cost)
        parcel.writeValue(mana_cost)
        parcel.writeValue(attack)
        parcel.writeValue(armor)
        parcel.writeValue(hit_points)
        parcel.writeByte(if (is_included) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CardListItem> {
        override fun createFromParcel(parcel: Parcel): CardListItem {
            return CardListItem(parcel)
        }

        override fun newArray(size: Int): Array<CardListItem?> {
            return arrayOfNulls(size)
        }
    }
}

