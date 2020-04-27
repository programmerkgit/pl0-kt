package org.example.automaton

val priorities = mapOf<Char, Int>(
    '^' to 0,
    '$' to 1,
    ')' to 2,
    '(' to 2,
    '|' to 3,
    '_' to 4, //ab => a_bと解釈
    '*' to 5,
    '?' to 5
)

