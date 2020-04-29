package org.example.automaton.dfa

import org.example.automaton.State

class DFAState(
    val stateSet: MutableSet<State> = mutableSetOf(),
    isStart: Boolean = false,
    isFinal: Boolean = false
) : State(isStart, isFinal) {

}
