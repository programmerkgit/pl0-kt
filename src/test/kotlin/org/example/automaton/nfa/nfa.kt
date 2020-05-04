package org.example.automaton

import org.example.automaton.nfa.NFA
import org.example.automaton.nfa.toPostFix
import org.junit.Test
import org.junit.Assert.assertEquals


class NFATest {

    @Test
    fun testNFANullMoveSet() {
        val pattern = "ab*"
        val nfa = NFA(pattern)
        val stateList = nfa.stateList
        val state = stateList[1]
        val set = nfa.getNullMoveStatesSet(state)
        assertEquals(set, setOf(state, nfa.getFinalState(), stateList[2]))
    }

    @Test
    fun getNullMoveStateSet() {
        val pattern = "ab*"
        val nfa = NFA(pattern)
        val stateList = nfa.stateList
        val state = stateList[0]
        val set = nfa.getNullMoveStatesSet(nfa.getGotoStateSet(state, 'a'))
        assertEquals(set, setOf(stateList[1], stateList[2], stateList[4]))
    }

    @Test
    fun getGotoStateSet() {
        val pattern = "ab*"
        val nfa = NFA(pattern)
        val stateList = nfa.stateList
        val state = stateList[0]
        val set = nfa.getGotoStateSet(state, 'a')
        assertEquals(set, setOf(stateList[1]))
    }

}