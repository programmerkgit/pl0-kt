package org.example.automaton2


class NFA : Automaton {

    val inputs = mutableSetOf<Char>()

    /* getterにしたい　*/
    private var start: NFAState

    constructor(startState: NFAState, newInputs: Set<Char>) {
        start = startState
        inputs.addAll(newInputs)
    }

    constructor(char: Char? = null) {
        start = when (char) {
            null -> {
                val firstState = NFAState(isStart = true)
                val finalState = NFAState(isFinal = true)
                firstState.addEpsilon(finalState)
                firstState
            }
            else -> {
                inputs.add(char)
                val firstState = NFAState(isStart = true)
                val finalState = NFAState(isFinal = true)
                firstState.add(char, finalState)
                firstState
            }
        }
    }

    constructor(pattern: String) {
        start = toNFA(pattern).start
        pattern.forEach { inputs.add(it) }
    }


    /* getterにしたい */
    private val final: NFAState
        get() = start.findFinal()


    fun deepCopy(): NFA {
        val newStart = start.deepCopy()
        return NFA(newStart, inputs)
    }

    /* sAf sBf => sAfBf */
    operator fun plus(other: NFA): NFA {
        val a = deepCopy()
        val b = other.deepCopy()
        val aFinal = a.final
        val bStart = b.start
        aFinal.addEpsilon(bStart)
        aFinal.isFinal = false
        bStart.isStart = false
        return NFA(a.start, a.inputs + b.inputs)
    }

    fun retrieveState(): Set<NFAState> {
        return start.retrieveState()
    }

    infix fun or(other: NFA): NFA {
        val a = deepCopy()
        val b = other.deepCopy()
        val aFinal = a.final
        val bFinal = b.final
        val aStart = a.start
        val bStart = b.start
        val firstState = NFAState(isStart = true)
        val finalState = NFAState(isFinal = true)
        firstState.addEpsilon(aStart)
        firstState.addEpsilon(bStart)
        aFinal.addEpsilon(finalState)
        bFinal.addEpsilon(finalState)
        aStart.isStart = false
        bStart.isStart = false
        aFinal.isFinal = false
        bFinal.isFinal = false
        return NFA(start, a.inputs + b.inputs)
    }

    fun positiveClosure(): NFA {
        return deepCopy() + closure()
    }

    fun closure(): NFA {
        val cp = deepCopy()
        val cpStart = cp.start
        val cpFinal = cp.final
        cpStart.isStart = false
        cpFinal.isFinal = false

        /* closure */
        val start = NFAState(isStart = true)
        val final = NFAState(isFinal = true)
        /* 1*/
        cpFinal.addEpsilon(cpStart)
        cpFinal.addEpsilon(final)
        /* 2 */
        start.addEpsilon(cpStart)
        start.addEpsilon(final)
        return NFA(start, cp.inputs)
    }

    fun nullable(): NFA {
        val cp = deepCopy()
        cp.start.addEpsilon(cp.final)
        return NFA(cp.start, inputs)
    }

    fun toDFA(): DFA {
        val cp = deepCopy()
        val startState = DFAState(isStart = true, nfaStateSet = cp.start.retrieveEpsilonTransitions())
        return DFA(startState, cp.inputs)
    }
}