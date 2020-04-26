package org.example.token

import org.junit.Assert.assertEquals
import org.junit.Test


class TokenMatcherTest {
    @Test
    fun testMatchers() {
        for (i in -100 until 100) {
            val token = checkNotNull(TokenMatcher.Integer.parse("$i"))
            assertEquals(token.value, "$i")
        }
    }
}
