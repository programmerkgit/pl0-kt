package org.example.automaton.nfa

import org.junit.Test
import org.junit.Assert.assertEquals


class TestPreProcess {
    @Test
    fun testPreProcess() {
        val pattern = "ab+"
        val expected = "a.b+"
        assertEquals(expected, preprocessPattern(pattern))
    }


}