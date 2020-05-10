package org.example.automaton2

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


    operator fun get(char: Char): Set<NFAState> {
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
