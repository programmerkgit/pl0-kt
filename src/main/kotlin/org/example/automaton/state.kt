package org.example.automaton

open class AutomatonState(
    var isStart: Boolean = false,
    var isFinal: Boolean = false,
    var id: String = if (isStart) "i" else if (isFinal) "f" else ""
) {

}