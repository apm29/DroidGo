package io.github.apm29.driodgo.model.artifact

import org.junit.Assert.*
import org.junit.Test
import java.util.*


class CardTypeTest {
    @Test
    fun testType() {
        val cardType = CardType.getType("Hero", null)
        assertTrue(cardType is CardType.Hero)
        assertTrue(CardType.getType("Item", "Deed") is CardType.Item.Deed)
    }

    @Test
    fun test() {
        val deckContents = Deck(
            arrayListOf(
                Card(10021, 1, 1),
                Card(10013, 1, 1),
                Card(10013, 1, 1),
                Card(10013, 1, 1),
                Card(10034, 1, 1)
            ),
            arrayListOf(
                Card(10022, 1, 1),
                Card(10012, 1, 1),
                Card(10012, 1, 1),
                Card(10012, 1, 1),
                Card(10012, 1, 1),
                Card(10012, 1, 1),
                Card(10033, 1, 1),
                Card(10033, 1, 1),
                Card(10033, 1, 1),
                Card(10033, 1, 1),
                Card(10041, 1, 1)
            ),
            "自定义套牌"
        )

        println(37 shr 4)

        println(1 and 2 - 1)
        println((1 and 2) - 1)
    }
}