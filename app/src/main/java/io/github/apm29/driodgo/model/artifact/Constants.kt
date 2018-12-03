package io.github.apm29.driodgo.model.artifact

import io.github.apm29.driodgo.model.artifact.bean.CardListItem
import java.lang.IllegalArgumentException

sealed class CardType(val type: String) {

    class Hero : CardType("Hero")

    class Creep : CardType("Creep")

    class Improvement : CardType("Improvement")

    sealed class Item(val subType: String) : CardType("Item") {

        class Weapon : Item("Weapon")

        class Armor : Item("Armor")

        class Accessory : Item("Accessory")

        class Deed : Item("Deed")

        class Consumable : Item("Consumable")
    }

    class Spell : CardType("Spell")

    companion object {
        fun getType(type: String?, subType: String?): CardType {
            return when (type) {
                "Hero" -> Hero()
                "Creep" -> Creep()
                "Improvement" -> Improvement()
                "Spell" -> Spell()
                "Item" -> when (subType) {
                    "Weapon" -> Item.Weapon()
                    "Armor" -> Item.Armor()
                    "Accessory" -> Item.Accessory()
                    "Deed" -> Item.Deed()
                    "Consumable" -> Item.Consumable()
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