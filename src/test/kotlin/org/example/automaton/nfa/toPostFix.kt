package org.example.automaton.nfa

import org.junit.Test
import org.junit.Assert.assertEquals


class ToPostFixTest {
    @Test
    fun testInsertConcatOperator() {
        val pattern = "a.b.(c.d|(e.f)*)*"
        val expected = "ab.cd.ef.*|*."
        assertEquals(expected, toPostFix(pattern))
    }

}