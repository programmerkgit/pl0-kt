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
        val pattern = "ab*((c*)d(e|f*)*g|d)"
        val expected = "ab*_c*)(d_ef*|*)(_g_d|)(_"
        assertEquals(expected, createReverseStack(pattern))
    }

    @Test
    fun testReverse3() {
        val pattern = "a|b*"
        val expected = "ab*|"
        assertEquals(expected, createReverseStack(pattern))
    }
    @Test
    fun testReverse4() {
        val pattern = "a(b|c)*"
        val expected = "abc|*)(_"
        assertEquals(expected, createReverseStack(pattern))
    }
    @Test
    fun testReverse5() {
        val pattern = "a(b|c)*d"
        val expected = "abc|*)(_d_"
        assertEquals(expected, createReverseStack(pattern))
    }
}