/*
package org.example.automaton

class State(
    var isStart: Boolean = false,
    var isFinal: Boolean = false
) {
}

*/
/* εによる移動を表現可能にする *//*

*/
/* 自分が変化しないようにロジックを書き換える *//*

class NFA(char: Char) {

    val transitionFunctions: MutableMap<State, MutableMap<Char, State>> = mutableMapOf()

    var stateSet: MutableSet<State> = mutableSetOf()

    private fun getFinalState(): State {
        return checkNotNull(stateSet.find { it.isFinal })
    }

    private fun replaceState(replacedState: State, newState: State) {
        */
/* change state function's destination *//*

        transitionFunctions.entries.forEach { transitionFunction ->
            transitionFunction.value.entries.forEach { m ->
                if (m.value == replacedState) {
                    m.setValue(newState)
                }
            }
        }
        */
/* change key of mapping *//*

        val function = transitionFunctions.remove(replacedState)
        if (function != null) {
            transitionFunctions[newState] = function
        }
        */
/* change state set *//*

        stateSet.remove(replacedState)
        stateSet.add(newState)
    }

    private fun getStartState(): State {
        return checkNotNull(stateSet.find {
            it.isStart
        })
    }

    private fun initializeNFA(char: Char) {
        when (char) {
            in Regex("[a-zA-Z]") -> {
                val startState = State(isStart = true)
                val finalState = State(isFinal = true)
                stateSet.add(startState)
                stateSet.add(finalState)
                transitionFunctions[startState] = mutableMapOf(char to finalState)
            }
        }
    }

    init {
        initializeNFA(char)
    }

    operator fun plus(nfa: NFA): NFA {
        */
/* should copy new nfa *//*

        val startState: State = State(isStart = true)
        val finalState: State = State(isFinal = true)
        val centerState: State = State()
        val thisStartState = getStartState()
        val thisFinalState = getFinalState()
        replaceState(thisStartState, startState)
        replaceState(thisFinalState, centerState)
        val nextStartState: State = nfa.getStartState()
        val nextFinalState: State = nfa.getFinalState()
        nfa.replaceState(nextStartState, centerState)
        nfa.replaceState(nextFinalState, finalState)
        stateSet = (stateSet + nfa.stateSet).toMutableSet()
        nfa.transitionFunctions.entries.forEach {
            if (transitionFunctions[it.key] === null) {
                transitionFunctions[it.key] = it.value
            } else {
                it.value.entries.forEach { m ->
                    transitionFunctions[it.key]?.set(m.key, m.value)
                }
            }
        }
        return this
    }

    override fun toString(): String {
        val states = stateSet.mapIndexed { i, state ->
            "$i: $state start: ${state.isStart} final: ${state.isFinal}"
        }.joinToString("\n")
        val functions = transitionFunctions.map {
            "${it.key}: " + it.value.map { it2 ->
                "${it2.key} => ${it2.value}"
            }.joinToString("\n")
        }.joinToString("\n")
        return "$states\n$functions"
    }
//    fun closure(): NFA {
//    }
//
//    infix fun or(nfa: NFA): NFA {
//    }
}

fun parseReversed(text: String) {
//    val stack: MutableList<NFA> = mutableListOf<NFA>()
//    for (char: Char in text) {
//        when (char) {
//            '*' -> {
//                val last = stack.removeAt(stack.lastIndex)
//                stack.add(last.closure())
//            }
//            '_' -> {
//                val b = stack.removeAt(stack.lastIndex)
//                val a = stack.removeAt(stack.lastIndex)
//                stack.add(a + b)
//            }
//            '|' -> {
//                val b = stack.removeAt(stack.lastIndex)
//                val a = stack.removeAt(stack.lastIndex)
//                stack.add(a or b)
//            }
//            '(' -> {
//                stack.removeAt(stack.lastIndex)
//            }
//            ')' -> {
//                stack.removeAt(stack.lastIndex)
//            }
//            in Regex("^[a-zA-Z]$") -> {
//                stack.add(NFA(char.toString()))
//            }
//        }
//
//    }
}

*/
/* example *//*

// val a = org.example.automaton.NFARegex("a") or org.example.automaton.NFARegex("b") + org.example.automaton.NFARegex("c").closure()

fun main() {
    println((NFA('a') + NFA('b') + NFA('c')).toString())
}*/
