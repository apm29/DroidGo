package io.github.apm29.driodgo

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

        var color = 0b0111
        var black = 0b1000

        println("check black : ${(color or black).toString(2)}")
        println("uncheck black : ${(color and  (black.inv())).toString(2)}")
        println("is  black checked: ${(color and black == black)}")
    }
}
