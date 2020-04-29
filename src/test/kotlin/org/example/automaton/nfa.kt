package org.example.automaton

import org.example.automaton.nfa.NFA
import org.example.automaton.nfa.reverseRegex
import org.junit.Test
import org.junit.Assert.assertEquals


class NFATest {
    @Test
    fun testReverse1() {
        val pattern = "a|b"
        val expected = "ab|"
        assertEquals(expected, reverseRegex(pattern))
    }

    @Test
    fun testReverse2() {
        val pattern = "ab*((c*)d(e|f*)*g|d)"
        val expected = "ab*_c*)(d_ef*|*)(_g_d|)(_"
        assertEquals(expected, reverseRegex(pattern))
    }

    @Test
    fun testReverse3() {
        val pattern = "a|b*"
        val expected = "ab*|"
        assertEquals(expected, reverseRegex(pattern))
    }

    @Test
    fun testReverse4() {
        val pattern = "a(b|c)*"
        val expected = "abc|*)(_"
        assertEquals(expected, reverseRegex(pattern))
    }

    @Test
    fun testReverse5() {
        val pattern = "a(b|c)*d"
        val expected = "abc|*)(_d_"
        assertEquals(expected, reverseRegex(pattern))
    }

    @Test
    fun testNFA() {
        val pattern = "a(b|c)*d"
        val expected = "abc|*)(_d_"
        println(NFA(pattern))
    }

    @Test
    fun testNFANullMoveSet() {
        val pattern = "ab*"
        val nfa = NFA(pattern)
        val stateList = nfa.stateList
        val state = stateList[1]
        val set = nfa.findNullMoveStatesSet(state)
        println(nfa)
        assertEquals(set, setOf(state, nfa.getFinalState(), stateList[2]))
    }

}