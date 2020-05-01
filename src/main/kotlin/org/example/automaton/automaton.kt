package org.example.automaton

import org.example.automaton.nfa.NFAState

/* εによる移動を表現可能にする */
/* 自分が変化しないようにロジックを書き換える */
/**
 * Non Deterministic Finite Automaton class.
 * @param[char] regex char
 */
abstract class Automaton<T: AutomatonState> {
    abstract val transitionFunctions: MutableMap<T, MutableMap<Char, MutableSet<T>>>
    open val stateList: MutableList<T> = mutableListOf()
    open val inputs: MutableSet<Char> = mutableSetOf()
}

val n: MutableSet<in AutomatonState> = mutableSetOf()

