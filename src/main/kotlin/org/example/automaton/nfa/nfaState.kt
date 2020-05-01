package org.example.automaton.nfa

import org.example.automaton.AutomatonState

open class NFAState(
    isStart: Boolean = false,
    isFinal: Boolean = false
) : AutomatonState(isStart, isFinal) {
}
