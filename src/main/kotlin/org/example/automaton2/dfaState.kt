package org.example.automaton2

class DFAState(
    isStart: Boolean = false,
    isFinal: Boolean = false,
    val nfaStateSet: Set<NFAState> = setOf()
) : AutomatonState(isStart, isFinal) {

    /* stateが作られた時,遷移可能なDFAStateを全て作成する */
    override fun copy(): DFAState {
        return DFAState(
            isStart,
            isFinal,
            nfaStateSet
        )
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is DFAState -> (nfaStateSet - other.nfaStateSet).isEmpty()
            else -> false
        }
    }
}
