package org.example.automaton.nfa

val opPrecedence = mapOf(
    '|' to 0,
    '.' to 1,
    '*' to 2,
    '?' to 2,
    '+' to 2
)
