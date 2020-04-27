package org.example.automaton

import org.junit.Test
import org.junit.Assert.assertEquals


class NFATest {
    @Test
    fun testReverse1() {
        val pattern = "a|b"
        val expected = "ab|"
        assertEquals(expected, createReverseStack(pattern))
    }

    @Test
    fun testReverse2() {
        val pattern = "ab(cd(e|f)g|d)"
        val expected = "ab+cd+ef|()g+d|()"
        assertEquals(expected, createReverseStack(pattern))
    }
}