package org.example.automaton.nfa

import java.util.regex.Pattern


private fun toNFA2(pattern: String): NFA2 {
    if (pattern.isEmpty())
        return NFA2('ε')
    val postFix = toPostFix(preprocessPattern(pattern))
    val stack = mutableListOf<NFA2>()
    postFix.forEach { c ->
        when (c) {
            in setOf('+', '*', '?') -> {
                val top = stack.removeAt(stack.lastIndex)
                when (c) {
                    '+' -> stack.add(top.positiveClosure())
                    '*' -> stack.add(top.closure())
                    '?' -> stack.add(top.nullable())
                }
            }
            in setOf('|', '.') -> {
                val b = stack.removeAt(stack.lastIndex)
                val a = stack.removeAt(stack.lastIndex)
                when (c) {
                    '|' -> stack.add(a or b)
                    '.' -> stack.add(a + b)
                }
            }
            else -> {
                stack.add(NFA2(c))
            }
        }
    }
    return stack.last()
}


class State2(
    var isStart: Boolean = false,
    var isFinal: Boolean = false,
    var transitions: MutableMap<Char, MutableList<State2>> = mutableMapOf()
) {
    fun copy(): State2 {
        val newState = State2(isStart, isFinal)
        transitions.forEach { char, states ->
            newState.transitions[char] = states.map { it.copy() }.toMutableList()
        }
        return newState
    }
}

class NFA2(char: Char? = null) {

    var stateList: MutableList<State2> = mutableListOf(
        State2(isStart = true), State2(isFinal = true)
    )

    fun inputs(): List<Char> {
        return stateList.flatMap { it.transitions.keys }.toSet().toList()
    }

    fun startState(): State2 {
        return stateList[0]
    }

    fun removeStartState(): State2 {
        return stateList.removeAt(0)
    }

    fun finalState(): State2 {
        return stateList.last()
    }

    fun removeFinalState(): State2 {
        return stateList.removeAt(stateList.lastIndex)
    }


    init {
        when (char) {
            null -> {
                val state = startState()
                val finalState = finalState()
                state.transitions.getOrPut('ε') { mutableListOf() }.add(finalState)
            }
            else -> {
                val state = startState()
                val finalState = finalState()
                state.transitions.getOrPut('ε') { mutableListOf() }.add(finalState)
            }
        }
    }

    fun addState(state2: State2) {
        this.stateList.add(stateList.lastIndex - 1, state2)
    }


    fun replaceStartState(newState: State2) {
        val oldState = removeStartState()
        oldState.transitions.forEach { (char, stateList) ->
            newState.transitions.getOrPut(char) { mutableListOf() }.addAll(stateList.map { it.copy() })
        }
        stateList.add(0, newState)
    }

    fun replaceFinalState(newState: State2) {
        val oldState = removeFinalState()
        oldState.transitions.forEach { (char, stateList) ->
            newState.transitions.getOrPut(char) { mutableListOf() }.addAll(stateList.map { it.copy() })
        }
        stateList.add(newState)
    }

    private fun copy(): NFA2 {
        val newNFA = NFA2()
        newNFA.stateList = stateList.map { it.copy() }.toMutableList()
        return newNFA
    }

    constructor(pattern: String) : this() {
        this.stateList = toNFA2(pattern).stateList
    }

    infix fun or(other: NFA2): NFA2 {
        val a = copy()
        val b = other.copy()
        b.replaceStartState(a.startState())
        b.replaceFinalState(a.finalState())
        b.stateList.forEach {
            if (it !in a.stateList) {
                a.addState(it)
            }
        }
        return a
    }

    operator fun plus(other: NFA2): NFA2 {
        val a = copy()
        val b = other.copy()
        val final = a.finalState()
        final.isFinal = false
        b.replaceStartState(final)
        b.stateList.forEach {
            if (it !in a.stateList) {
                a.addState(it)
            }
        }
        return a
    }

    fun closure(): NFA2 {
        val a = copy()
        val start = State2(isStart = true)
        val final = State2(isFinal = true)
        val center1 = State2()
        val center2 = State2()
        start.transitions.getOrPut('ε') { mutableListOf() }.add(center1)
        start.transitions.getOrPut('ε') { mutableListOf() }.add(final)
        center2.transitions.getOrPut('ε') { mutableListOf() }.add(center1)
        center2.transitions.getOrPut('ε') { mutableListOf() }.add(final)
        a.replaceStartState(center1)
        a.replaceFinalState(center2)
        a.addState(start)
        a.addState(final)
        return a
    }

    fun nullable(): NFA2 {
        return this or NFA2('ε')
    }

    fun positiveClosure(): NFA2 {
        return this + closure()
    }
}