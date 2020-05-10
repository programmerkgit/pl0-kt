package org.example.automaton2

import org.example.automaton.nfa.DFA2
import org.example.automaton.nfa.DFAState2


class DFA(
    startState: DFAState,
    val inputs: MutableSet<Char> = mutableSetOf(),
    val transtions: MutableMap<Char, DFAState> = mutableMapOf()
) : Automaton() {
    val stateSet: MutableSet<DFAState> = mutableSetOf()

    /* 初期状態から遷移表を作成する */
    init {
        stateSet.add(startState)
        val list = stateSet.toMutableList()
        var i = 0;
        while (i < list.size) {
            val currentState = list[i]
            inputs.forEach { c ->
                val nextStateSet = currentState.nfaStateSet.flatMap { s ->
                    s[c].flatMap { it.retrieveEpsilonTransitions() }
                }.toSet()
                val isFinal = nextStateSet.any { it.isFinal }
                val dfa = DFAState(
                    isFinal = isFinal,
                    nfaStateSet = nextStateSet
                )
                if (list.contains(dfa)) {
                    transtions[c] == list.find { it == dfa }
                } else {
                    transtions[c] = dfa
                    list.add(dfa)
                }
            }
            i++
        }
        stateSet.addAll(list)
    }
}