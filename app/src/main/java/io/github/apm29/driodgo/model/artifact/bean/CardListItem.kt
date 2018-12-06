package io.github.apm29.driodgo.model.artifact.bean

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.content.ContextCompat
import io.github.apm29.driodgo.R

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
) {
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
}

