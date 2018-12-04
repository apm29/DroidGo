package io.github.apm29.driodgo.model.artifact.bean

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

    var is_included:Boolean = false//是否是其他卡牌附带
)

