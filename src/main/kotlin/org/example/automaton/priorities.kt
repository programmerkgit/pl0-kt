package org.example.automaton

val priorities = mapOf<Char, Int>(
    '^' to 0,
    '$' to 1,
    '(' to 2,
    ')' to 3,
    '|' to 4,
    '_' to 5, //ab => a_bと解釈
    '*' to 6,
    '?' to 6
)

