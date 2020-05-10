package org.example.automaton2

abstract class AutomatonState(
    var isStart: Boolean = false,
    var isFinal: Boolean = false
) {
    abstract fun copy(): Any
}
