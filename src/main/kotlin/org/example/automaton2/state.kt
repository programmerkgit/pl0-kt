package org.example.automaton2

abstract class AutomatonState(
    var isStart: Boolean = false,
    var isFinal: Boolean = false
) {


    abstract operator fun get(char: Char): Any
    abstract fun copy(): Any
}

class NFAState(
    isStart: Boolean = false,
    isFinal: Boolean = false,
    private val transitions: MutableMap<Char, MutableSet<NFAState>> = mutableMapOf(),
    private val epsilonSet: MutableSet<NFAState> = mutableSetOf()
) : AutomatonState(isStart, isFinal) {

    val inputs: Set<Char>
        get() = transitions.keys.toSet()

    init {
        epsilonSet.add(this)
    }

    /* Find all final states. To prevent loop, if checked, return empty set even if it has final state */
    fun findFinal(): NFAState {
        return checkNotNull(retrieveState().find { it.isFinal })
    }

    fun retrieveState(accum: Set<NFAState> = setOf()): Set<NFAState> {
        return if (accum.contains(this)) {
            accum
        } else {
            nextSet().flatMap { it.retrieveState(accum) }.toSet()
        }
    }

    fun nextSet(): Set<NFAState> {
        return transitions.flatMap { it.value }.toSet() + (nextEpsilonTransitions() - this)
    }

    override fun copy(): NFAState {
        return NFAState(
            isStart,
            isFinal,
            transitions,
            epsilonSet
        )
    }

    fun deepCopy(): NFAState {
        val new = copy()
        new.transitions.forEach { (char, stateSet) ->
            new[char] = stateSet.map { it.deepCopy() }.toSet()
        }
        return new
    }

    fun nextEpsilonTransitions(): Set<NFAState> {
        return epsilonSet + this
    }

    fun retrieveEpsilonTransitions(accum: Set<NFAState> = setOf()): Set<NFAState> {
        return if (accum.contains(this)) {
            accum
        } else {
            nextSet().flatMap { it.retrieveEpsilonTransitions(accum + nextEpsilonTransitions()) }.toSet()
        }
    }

    fun addEpsilon(state: NFAState) {
        epsilonSet.add(state)
    }


    override operator fun get(char: Char): Set<NFAState> {
        return transitions[char] ?: setOf()
    }


    operator fun set(char: Char, value: Set<NFAState>) {
        transitions[char] = value.toMutableSet()
    }

    fun add(char: Char, nfaState: NFAState) {
        transitions.getOrPut(char) { mutableSetOf() }.add(nfaState)
    }

    fun addAll(char: Char, nfaState: Set<NFAState>) {
        transitions.getOrPut(char) { mutableSetOf() }.addAll(nfaState)
    }

}

class DFAState(
    isStart: Boolean = false,
    isFinal: Boolean = false,
    val nfaStateSet: Set<NFAState> = setOf(),
    private val transitions: MutableMap<Char, DFAState> = mutableMapOf()
) : AutomatonState(isStart, isFinal) {

    /* stateが作られた時,遷移可能なDFAStateを全て作成する */

    override fun copy(): DFAState {
        return DFAState(
            isStart,
            isFinal,
            nfaStateSet,
            transitions
        )
    }

    override operator fun get(char: Char): DFAState {
        return if (transitions[char] == null) {
            val nextState = nfaStateSet.flatMap { it[char] }.flatMap { it.retrieveEpsilonTransitions() }.toSet()
            val final = nextState.any { it.isFinal }
            val dfa = DFAState(isFinal = final, nfaStateSet = nextState)
            transitions[char] = dfa
            return dfa
        } else {
            checkNotNull(transitions[char])
        }
    }

    operator fun set(char: Char, dfaState: DFAState) {
        transitions[char] = dfaState
    }

    fun getOrPut(char: Char, f: () -> DFAState): DFAState {
        return transitions.getOrPut(char) { f() }
    }
}
