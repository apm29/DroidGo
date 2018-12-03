package io.github.apm29.driodgo

import android.text.Html
import androidx.test.InstrumentationRegistry.*
import androidx.test.runner.AndroidJUnit4
import io.github.apm29.driodgo.model.artifact.CardEncoder
import io.github.apm29.driodgo.model.artifact.Card
import io.github.apm29.driodgo.model.artifact.Deck

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = getTargetContext()
        assertEquals("io.github.apm29.driodgo", appContext.packageName)

        val html = Html.fromHtml("<a 'href'= 'baidu'> ad</a>")
        println(html.toString())
    }

    @Test
    fun testCard(){
        val deck: Deck = Deck(
            arrayListOf(
                Card(1,1,16),
                Card(2,1,18)
            ),
            arrayListOf(
                Card(1,1,6),
                Card(2,1,8)
            ),
            "wang"
        )

        println(CardEncoder().EncodeDeck(deck))
    }
}
