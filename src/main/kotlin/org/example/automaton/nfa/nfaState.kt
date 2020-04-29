package org.example.automaton.nfa

import org.example.automaton.State

open class NFAState(
    isStart: Boolean = false,
    isFinal: Boolean = false
) : State(isStart, isFinal) {
}
