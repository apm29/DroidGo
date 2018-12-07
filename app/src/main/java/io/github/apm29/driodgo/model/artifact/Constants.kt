package io.github.apm29.driodgo.model.artifact

import io.github.apm29.driodgo.model.artifact.bean.CardListItem
import java.lang.IllegalArgumentException

sealed class CardType(val type: String) {

    object Hero : CardType("Hero")

    object Creep : CardType("Creep")

    object Improvement : CardType("Improvement")

    sealed class Item(val subType: String) : CardType("Item") {

        object Weapon : Item("Weapon")

        object Armor : Item("Armor")

        object Accessory : Item("Accessory")

        object Deed : Item("Deed")

        object Consumable : Item("Consumable")
    }

    object Spell : CardType("Spell")

    companion object {
        fun getType(type: String?, subType: String?): CardType {
            return when (type) {
                "Hero" -> Hero
                "Creep" -> Creep
                "Improvement" -> Improvement
                "Spell" -> Spell
                "Item" -> when (subType) {
                    "Weapon" -> Item.Weapon
                    "Armor" -> Item.Armor
                    "Accessory" -> Item.Accessory
                    "Deed" -> Item.Deed
                    "Consumable" -> Item.Consumable
                    else -> throw IllegalArgumentException("未知卡牌类型 $type - $subType")
                }
                else -> throw IllegalArgumentException("未知卡牌类型 $type - $subType")
            }
        }

        private const val UNKNOWN_TYPE = "Unknown Type"

        fun getType(cardListItem: CardListItem): CardType {
            return getType(cardListItem.card_type ?: UNKNOWN_TYPE, cardListItem.sub_type ?: UNKNOWN_TYPE)
        }
    }
}