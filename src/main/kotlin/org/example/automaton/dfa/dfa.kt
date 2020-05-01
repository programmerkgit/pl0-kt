package org.example.automaton.dfa

import org.example.automaton.nfa.NFAState
import org.example.automaton.nfa.contains
import org.example.automaton.nfa.parseReversed
import org.example.automaton.nfa.reverseRegex

/* εによる移動を表現可能にする */
/* 自分が変化しないようにロジックを書き換える */
/**
 * Non Deterministic Finite Automaton class.
 * @param[char] regex char
 */
class DFA(
    val stateList: MutableList<DFAState> = mutableListOf(),
    val transitionFunctions: MutableMap<DFAState, MutableMap<Char, DFAState>> = mutableMapOf(),
    var inputs: MutableSet<Char>
) {

    init {
        inputs.remove('ε')
        setStateNumbers()
    }

    fun setStateNumbers() {
        var count = 1
        stateList.forEach {
            when {
                it.isStart -> it.id = "i"
                else -> {
                    it.id = count.toString() + if (it.isFinal) "f" else ""
                    count++
                }
            }
        }
    }


    override fun toString(): String {
        val header = "DFA"
        val stateDescription = "State: " + stateList.joinToString(" ") { DFAState ->
            "${DFAState.id}:(${DFAState.stateSet.joinToString(",") { it.id }})"
        }

        val colWidth = 5
        /* what to get max value */
        var table = "|".padStart(15)
        table += inputs.joinToString("|") { it.toString().padEnd(colWidth) }
        table += "\n"

        table += stateList.joinToString("\n") { dfaState ->
            var line = "(${dfaState.stateSet.joinToString(",") { it.id }}):${dfaState.id}|".padStart(15)
            line += inputs.joinToString("|") { input ->
                transitionFunctions.getOrDefault(dfaState, mutableMapOf())
                    .getOrDefault(input, DFAState(setOf())).id.padEnd(colWidth)
            }
            line
        }

        return "$header\n$stateDescription\n$table"
    }
}
