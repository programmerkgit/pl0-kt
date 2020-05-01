package org.example.automaton.dfa

import org.example.automaton.AutomatonState
import org.example.automaton.nfa.NFAState

class DFAState(
    val stateSet: Set<NFAState> = mutableSetOf(),
    isStart: Boolean = false,
    isFinal: Boolean = false
) : AutomatonState(isStart, isFinal) {

}
