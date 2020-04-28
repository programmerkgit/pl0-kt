package org.example.automaton

class State(
    var isStart: Boolean = false,
    var isFinal: Boolean = false
) {
}

/* εによる移動を表現可能にする */
/* 自分が変化しないようにロジックを書き換える */
/**
 * Create Non Deterministic Finite Automaton from regex char or string.
 * @param[char] regex char
 *
 */
class NFA(char: Char = ' ') {

    init {
        initializeNFA(char)
    }

    /**
     * @constructor[string] create non deterministic finite automaton from regex string
     */
    constructor(string: String) : this() {
        this.import(parseReversed(reverseRegex(string)))
    }

    private val transitionFunctions: MutableMap<State, MutableMap<Char, MutableSet<State>>> = mutableMapOf()
    private val stateSet: MutableSet<State> = mutableSetOf()

    private fun getFinalState(): State {
        return checkNotNull(stateSet.find { it.isFinal })
    }

    /**
     * Create copy of this nfa.
     * @return copied NFA
     */
    private fun copy(): NFA {
        /* change state function's destination */
        val newNFA = NFA();
        /* copy transitionFunctions */
        newNFA.import(this)
        return newNFA
    }

    /**
     * replace start state. State set and transition functions's states are replaced with [newState].
     * @param[newState] newState.
     */
    private fun replaceStartState(newState: State) {
        val startState = getStartState()
        replaceState(startState, newState)
    }

    /**
     * replace final state. State set and transition functions's states are replaced with [newState].
     * @param[newState] newState.
     */
    private fun replaceFinalState(newState: State) {
        val finalState = getFinalState()
        replaceState(finalState, newState)
    }

    private fun replaceState(replacedState: State, newState: State) {
        /* change key of mapping */
        transitionFunctions.forEach { (_, raw) ->
            raw.forEach { (_, stateSet) ->
                if (stateSet.remove(replacedState)) {
                    stateSet.add(newState)
                }
            }
        }
        val function = transitionFunctions.remove(replacedState)
        if (function != null) {
            transitionFunctions[newState] = function
        }
        /* change state set */
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
                transitionFunctions[startState] = mutableMapOf(char to mutableSetOf(finalState))
            }
            'Ω' -> {
                val startState = State(isStart = true)
                val finalState = State(isFinal = true)
                stateSet.add(startState)
                stateSet.add(finalState)
                transitionFunctions[startState] = mutableMapOf('Ω' to mutableSetOf(finalState))
            }
            ' ' -> {
            }
        }
    }


    private fun import(nfa: NFA): NFA {
        nfa.stateSet.forEach {
            stateSet.add(it)
        }
        nfa.transitionFunctions.forEach { (keyState, raw) ->
            if (transitionFunctions[keyState] === null) {
                transitionFunctions[keyState] = mutableMapOf()
            }
            raw.forEach { (input, stateSet) ->
                if (transitionFunctions[keyState]!![input] === null) {
                    transitionFunctions[keyState]!![input] = mutableSetOf()
                }
                stateSet.forEach {
                    transitionFunctions[keyState]!![input]!!.add(it)
                }
            }
        }
        return this
    }

    private fun merge(nfa: NFA): NFA {
        val copyOfThis = copy()
        copyOfThis.import(nfa)
        return copyOfThis
    }

    operator fun plus(nfa: NFA): NFA {
        /* should copy new nfa */
        val copyOfThis = copy()
        val copyOfInput = nfa.copy()
        val startState: State = State(isStart = true)
        val finalState: State = State(isFinal = true)
        val centerState: State = State()
        copyOfThis.replaceStartState(startState)
        copyOfThis.replaceFinalState(centerState)
        copyOfInput.replaceStartState(centerState)
        copyOfInput.replaceFinalState(finalState)

        return copyOfThis.import(copyOfInput)
    }

    /* 表示方法を変えたい */
    override fun toString(): String {
        val states = stateSet.mapIndexed { i, state ->
            "$i: $state start: ${state.isStart} final: ${state.isFinal}"
        }.joinToString("\n")
        val functions = transitionFunctions.map { stateFunctionMap ->
            "${stateFunctionMap.key}: " + stateFunctionMap.value.map { map ->
                "${map.key} => ${map.value}"
            }.joinToString("\n")
        }.joinToString("\n")
        return "$states\n$functions"
    }

    fun closure(): NFA {
        val copyOfThis = copy()
        val nfa = NFA()
        val startState: State = State(isStart = true)
        val state1: State = State()
        val state2: State = State()
        val endState: State = State(isFinal = true)
        nfa.stateSet.add(startState)
        nfa.stateSet.add(state1)
        nfa.stateSet.add(state2)
        nfa.stateSet.add(endState)
        val startStateRaw = mutableMapOf('Ω' to mutableSetOf<State>(endState, state1))
        val state1Raw = mutableMapOf<Char, MutableSet<State>>()
        val state2Raw = mutableMapOf<Char, MutableSet<State>>('Ω' to mutableSetOf<State>(state1, endState))
        val endStateRow = mutableMapOf<Char, MutableSet<State>>()
        nfa.transitionFunctions[startState] = startStateRaw
        nfa.transitionFunctions[state1] = state1Raw
        nfa.transitionFunctions[state2] = state2Raw
        nfa.transitionFunctions[endState] = endStateRow
        copyOfThis.replaceStartState(state1)
        copyOfThis.replaceFinalState(state2)
        return nfa.import(copyOfThis)
    }

    infix fun or(nfa: NFA): NFA {
        val copyOfThis = copy()
        val copyOfInput = nfa.copy()
        val startState: State = State(isStart = true)
        val finalState: State = State(isFinal = true)
        copyOfThis.replaceStartState(startState)
        copyOfThis.replaceFinalState(finalState)
        copyOfInput.replaceStartState(startState)
        copyOfInput.replaceFinalState(finalState)
        return copyOfThis.import(copyOfInput)
    }
}
