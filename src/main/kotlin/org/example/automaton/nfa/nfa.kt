package org.example.automaton.nfa


import org.example.automaton.Automaton
import org.example.automaton.dfa.DFA
import org.example.automaton.dfa.DFAState

/* εによる移動を表現可能にする */
/* 自分が変化しないようにロジックを書き換える */
/**
 * Non Deterministic Finite Automaton class.
 * @param[char] regex char
 */
class NFA(char: Char = ' ') : Automaton<NFAState>() {
    override val transitionFunctions: MutableMap<NFAState, MutableMap<Char, MutableSet<NFAState>>> = mutableMapOf()
    override val stateList: MutableList<NFAState> = mutableListOf()

    /* inputsをRegexを読み込んだタイミングで生成する必要がある */
    override val inputs: MutableSet<Char> = mutableSetOf('ε')

    init {
        initializeNFA(char)
    }

    /**
     * @constructor[regexString] from which org.example.automaton.nfa.NFA is created.
     */
    constructor(regexString: String) : this() {
        import(
            parseReversed(reverseRegex(regexString))
        )
    }

    private fun addState(state: NFAState) {
        if (stateList.indexOf(state) == -1) {
            when {
                state.isStart -> {
                    stateList.add(0, state)
                    state.id = "s"
                }
                state.isFinal -> {
                    stateList.add(state)
                    state.id = "f"
                }
                else -> {
                    if (stateList.last().isFinal) {
                        stateList.add(stateList.lastIndex, state)
                    } else {
                        stateList.add(state)
                    }
                    setStateNumbers()
                }
            }
        }
    }

    fun getFinalState(): NFAState {
        return checkNotNull(stateList.find { it.isFinal })
    }

    /**
     * Create copy of this nfa.
     * @return copied org.example.automaton.nfa.NFA
     */
    private fun copy(): NFA {
        /* change state function's destination */
        val newNFA = NFA();
        /* copy transitionFunctions */
        newNFA.import(this)
        return newNFA
    }

    /**
     * replace start state. org.example.automaton.nfa.org.example.automaton.State set and transition functions's states are replaced with [newState].
     * @param[newState] newState.
     */
    private fun replaceStartState(newState: NFAState) {
        val startState = getStartState()
        replaceState(startState, newState)
    }

    /**
     * replace final state. org.example.automaton.nfa.org.example.automaton.State set and transition functions's states are replaced with [newState].
     * @param[newState] newState.
     */
    private fun replaceFinalState(newState: NFAState) {
        val finalState = getFinalState()
        replaceState(finalState, newState)
    }

    private fun replaceStateList(olsState: NFAState, newState: NFAState) {
        if (stateList.remove(olsState)) {
            addState(newState)
        }
    }

    private fun replaceStateSet(stateSet: MutableSet<NFAState>, oldState: NFAState, newState: NFAState) {
        if (stateSet.remove(oldState)) {
            stateSet.add(newState)
        }
    }

    private fun replaceState(oldState: NFAState, newState: NFAState) {
        /* change key of mapping */
        transitionFunctions.forEach { (_, raw) ->
            raw.forEach { (_, stateSet) ->
                replaceStateSet(stateSet, oldState, newState)
            }
        }
        val function = transitionFunctions.remove(oldState)
        if (function != null) {
            transitionFunctions[newState] = function
        }
        replaceStateList(oldState, newState)
    }

    private fun getStartState(): NFAState {
        return checkNotNull(stateList.find {
            it.isStart
        })
    }

    private fun initializeNFA(char: Char) {
        when (char) {
            in Regex("[a-zA-Z]") -> {
                val startState = NFAState(isStart = true)
                val finalState = NFAState(isFinal = true)
                addState(startState)
                addState(finalState)
                inputs.add(char)
                transitionFunctions[startState] = mutableMapOf(char to mutableSetOf(finalState))
            }
            'ε' -> {
                val startState = NFAState(isStart = true)
                val finalState = NFAState(isFinal = true)
                addState(startState)
                addState(finalState)
                inputs.add(char)
                transitionFunctions[startState] = mutableMapOf('ε' to mutableSetOf(finalState))
            }
            ' ' -> {
            }
        }
    }


    private fun import(nfa: NFA): NFA {
        nfa.stateList.forEach {
            addState(it)
        }
        inputs += nfa.inputs
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
        val startState: NFAState =
            NFAState(isStart = true)
        val finalState: NFAState =
            NFAState(isFinal = true)
        val centerState: NFAState =
            NFAState()
        copyOfThis.replaceStartState(startState)
        copyOfThis.replaceFinalState(centerState)
        copyOfInput.replaceStartState(centerState)
        copyOfInput.replaceFinalState(finalState)

        return copyOfThis.import(copyOfInput)
    }

    /**
     * Display as human readble style
     *
     */
    override fun toString(): String {
        val header = "NFA"
        val stateDescription = "State: " + stateList.joinToString(" ") { it.id }

        val maxColSize: Int = transitionFunctions.map { (_, m) ->
            m.map { it.value.size }.maxBy { it } ?: 0
        }.maxBy { it } ?: 0
        val colWidth = maxColSize * 2
        /* what to get max value */
        var functionDescription = "|".padStart(6)
        functionDescription += inputs.joinToString("|") { it.toString().padEnd(colWidth) }
        functionDescription += "\n"

        functionDescription += stateList.joinToString("\n") { state ->
            var line = "${state.id}|".padStart(6)
            line += inputs.joinToString("|") { input ->
                transitionFunctions.getOrDefault(state, mutableMapOf())
                    .getOrDefault(input, mutableSetOf()).joinToString(",") { it.id }.padEnd(colWidth)
            }
            line
        }

        return "$header\n$stateDescription\n$functionDescription"
    }

    fun closure(): NFA {
        val copyOfThis = copy()
        val nfa = NFA()
        val startState: NFAState =
            NFAState(isStart = true)
        val state1: NFAState =
            NFAState()
        val state2: NFAState =
            NFAState()
        val endState: NFAState =
            NFAState(isFinal = true)
        nfa.addState(startState)
        nfa.addState(state1)
        nfa.addState(state2)
        nfa.addState(endState)
        val startStateRaw = mutableMapOf('ε' to mutableSetOf<NFAState>(endState, state1))
        val state1Raw = mutableMapOf<Char, MutableSet<NFAState>>()
        val state2Raw = mutableMapOf('ε' to mutableSetOf(state1, endState))
        val endStateRow = mutableMapOf<Char, MutableSet<NFAState>>()
        nfa.transitionFunctions[startState] = startStateRaw
        nfa.transitionFunctions[state1] = state1Raw
        nfa.transitionFunctions[state2] = state2Raw
        nfa.transitionFunctions[endState] = endStateRow
        copyOfThis.replaceStartState(state1)
        copyOfThis.replaceFinalState(state2)
        return nfa.import(copyOfThis)
    }

    fun setStateNumbers() {
        var count = 1
        stateList.forEach {
            when {
                it.isFinal -> it.id = "f"
                it.isStart -> it.id = "i"
                else -> {
                    it.id = count.toString()
                    count++
                }
            }
        }
    }

    fun getNextDFAStateSet(prevStateSet: DFAState, input: Char): Set<NFAState> {
        return if (input == 'ε') {
            getNullMoveStatesSet(getGotoStateSet(prevStateSet.stateSet, input) + prevStateSet.stateSet)
        } else {
            getNullMoveStatesSet(getGotoStateSet(prevStateSet.stateSet, input))
        }
    }

    fun toDFA(): DFA {
        val newStateList: MutableList<DFAState> = mutableListOf()
        val newTransitionFunctions = mutableMapOf<DFAState, MutableMap<Char, DFAState>>()
        val firstState = getStartState()
        val next = getNullMoveStatesSet(firstState)
        newStateList.add(DFAState(next, isStart = true))
        var i = 0;
        while (i < newStateList.size) {
            inputs.forEach() {
                /* TODO: ロジックを綺麗にしたい */
                /* check if added or not */
                /* get dfa state */
                val newDFAState = DFAState(getNextDFAStateSet(newStateList[i], it))
                if (newDFAState.stateSet.isNotEmpty()) {
                    newDFAState.isFinal = newDFAState.stateSet.contains(getFinalState())
                    /* transition functionに追加 */
                    val existingOrNewDFAState =
                        newStateList.find { dfaState -> dfaState.stateSet == newDFAState.stateSet } ?: newDFAState
                    newTransitionFunctions.getOrPut(newStateList[i]) { mutableMapOf() }[it] = existingOrNewDFAState
                    if (newStateList.indexOf(existingOrNewDFAState) == -1) {
                        newStateList.add(existingOrNewDFAState)
                    }
                }
            }
            i++
        }
        return DFA(
            newStateList,
            newTransitionFunctions,
            inputs
        )
    }

    infix fun or(nfa: NFA): NFA {
        val copyOfThis = copy()
        val copyOfInput = nfa.copy()
        val startState = NFAState(isStart = true)
        val finalState = NFAState(isFinal = true)
        copyOfThis.replaceStartState(startState)
        copyOfThis.replaceFinalState(finalState)
        copyOfInput.replaceStartState(startState)
        copyOfInput.replaceFinalState(finalState)
        return copyOfThis.import(copyOfInput)
    }

    /**
     * get state set which can be moved from [state] with ε.
     * @param[state] base state.
     */
    fun getNullMoveStatesSet(state: NFAState): Set<NFAState> {
        return (getGotoStateSet(state, 'ε'))
    }

    /**
     *  get state set which can be moved from [stateSet] with ε.
     * @param[stateSet] Base State.
     */
    fun getNullMoveStatesSet(stateSet: Set<NFAState>): Set<NFAState> {
        return getGotoStateSet(stateSet, 'ε')
    }

    fun getGotoStateSet(state: NFAState, input: Char): MutableSet<NFAState> {
        return if (input == 'ε') {
            ((transitionFunctions[state]?.get(input) ?: mutableSetOf()) + state).toMutableSet()
        } else {
            transitionFunctions[state]?.get(input) ?: mutableSetOf()
        }
    }

    fun getGotoStateSet(stateSet: Set<NFAState>, input: Char): Set<NFAState> {
        val initialSet = if (input == 'ε') stateSet else setOf()
        return stateSet.fold(initialSet) { acc, nfaState ->
            acc + getGotoStateSet(nfaState, input)
        }
    }

    fun getGotoStateSetInputNotNull(stateSet: Set<NFAState>, input: Char): Set<NFAState> {
        return stateSet.fold(setOf()) { acc, nfaState ->
            acc + getGotoStateSet(nfaState, input)
        }
    }

}

fun main() {
    val nfa1 = NFA("ab|bc")
    val nfa2 = NFA("ab") or NFA("bc")
    println("NFA1")
    println(nfa1)
    println("NFA2")
    println(nfa2)
}

