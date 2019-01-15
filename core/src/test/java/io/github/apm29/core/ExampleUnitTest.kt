package io.github.apm29.core

import io.github.apm29.core.utils.toCentInteger
import io.github.apm29.core.utils.toDigitString
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val a :Double =  .0
        println(a.toCentInteger())
        println(a.toDigitString())
        assertEquals(4, (2 + 2).toLong())
    }
}