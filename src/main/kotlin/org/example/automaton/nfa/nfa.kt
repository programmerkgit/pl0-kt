package org.example.automaton.nfa

import javax.swing.plaf.nimbus.State

/* εによる移動を表現可能にする */
/* 自分が変化しないようにロジックを書き換える */
/**
 * Non Deterministic Finite Automaton class.
 * @param[char] regex char
 */
class NFA(char: Char = ' ') {
    val transitionFunctions: MutableMap<NFAState, MutableMap<Char, MutableSet<NFAState>>> = mutableMapOf()
    val stateList: MutableList<NFAState> = mutableListOf()

    /* inputsをRegexを読み込んだタイミングで生成する必要がある */
    val inputs: MutableSet<Char> = mutableSetOf()

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
                }
                state.isFinal -> {
                    stateList.add(state)
                }
                else -> {
                    if (stateList.last().isFinal) {
                        stateList.add(stateList.lastIndex, state)
                    } else {
                        stateList.add(state)
                    }
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
                transitionFunctions[startState] = mutableMapOf(char to mutableSetOf(finalState))
            }
            'ε' -> {
                val startState = NFAState(isStart = true)
                val finalState = NFAState(isFinal = true)
                addState(startState)
                addState(finalState)
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

        val stateDescription = stateList.mapIndexed { i, state ->
            val preFix = when {
                state.isStart -> {
                    "start"
                }
                state.isFinal -> {
                    "final"
                }
                else -> ""
            }
            "state: ${i}: $preFix"
        }.joinToString("\n")

        var functionDescription = ""
        transitionFunctions.forEach { (state, function) ->
            val target = checkNotNull(stateList.indexOf(state))
            function.forEach { (input, gotoStateSet) ->
                val stateMap = gotoStateSet.map { gotoState ->
                    checkNotNull(stateList.indexOf(gotoState))
                }
                functionDescription += "f(${target}, $input) => ${stateMap}\n"
            }
        }
        return "$stateDescription\n$functionDescription"
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

    fun toDNA() {
        val newStateList: MutableList<Set<NFAState>> = mutableListOf()
        val newTransitionFunctions = mutableMapOf<Set<NFAState>, MutableMap<Char, Set<NFAState>>>()
        val firstState = getStartState()
        val next = getNullMoveStatesSet(firstState)
        newStateList.add(next)
        var i = 0;
        while (i < newStateList.size) {
            inputs.forEach() {
                /* TODO: ロジックを綺麗にしたい */
                /* check if added or not */
                val newSet = getNullMoveStatesSet(getGotoStateSet(newStateList[i], it))
                /* transition functionに追加 */
                newTransitionFunctions.getOrPut(newStateList[i]) { mutableMapOf() }[it] = newSet
                if (newStateList.indexOf(newSet) == -1) {
                    newStateList.add(newSet)
                }
            }
            i++
        }
        println(newStateList)
        println(newTransitionFunctions)
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
        return (getGotoStateSet(state, 'ε')) + state
    }

    /**
     *  get state set which can be moved from [stateSet] with ε.
     * @param[stateSet] Base State.
     */
    fun getNullMoveStatesSet(stateSet: Set<NFAState>): Set<NFAState> {
        return getGotoStateSet(stateSet, 'ε') + stateSet
    }

    fun getGotoStateSet(stateSet: Set<NFAState>, input: Char): Set<NFAState> {
        return stateSet.fold(setOf()) { acc, nfaState ->
            acc + getGotoStateSet(nfaState, input)
        }
    }

    fun getGotoStateSet(state: NFAState, input: Char): MutableSet<NFAState> {
        return transitionFunctions[state]?.get(input) ?: mutableSetOf()
    }
}

fun main() {
    val pattern = "a(b|c)*e"
    val nfa = NFA(pattern)
    println(nfa.toDNA())
}

