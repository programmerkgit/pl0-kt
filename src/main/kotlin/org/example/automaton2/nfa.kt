package org.example.automaton2


class NFA : Automaton {

    /* getterにしたい　*/
    private var start: NFAState

    constructor(startState: NFAState) {
        start = startState
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
                val firstState = NFAState(isStart = true)
                val finalState = NFAState(isFinal = true)
                firstState.add(char, finalState)
                firstState
            }
        }
    }

    constructor(pattern: String) {
        start = toNFA(pattern).start
    }


    /* getterにしたい */
    private val final: NFAState
        get() = start.findFinal()


    fun deepCopy(): NFA {
        val newStart = start.deepCopy()
        return NFA(newStart)
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
        return NFA(a.start)
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
        return NFA(start)
    }

    fun positiveClosure(): NFA {
        return deepCopy() + closure()
    }

    fun closure(): NFA {
        val a = deepCopy()
        val aStart = a.start
        val aFinal = a.final
        aStart.isStart = false
        aFinal.isFinal = false

        /* closure */
        val start = NFAState(isStart = true)
        val final = NFAState(isFinal = true)
        /* 1*/
        aFinal.addEpsilon(aStart)
        aFinal.addEpsilon(final)
        /* 2 */
        start.addEpsilon(aStart)
        start.addEpsilon(final)
        return NFA(start)
    }

    fun nullable(): NFA {
        val a = deepCopy()
        a.start.addEpsilon(a.final)
        return NFA(a.start)
    }

    fun toDFA(): DFA {
        val a = deepCopy()
        val startState = DFAState(isStart = true, nfaStateSet = a.start.retrieveEpsilonTransitions())
        return DFA(startState)
    }
}