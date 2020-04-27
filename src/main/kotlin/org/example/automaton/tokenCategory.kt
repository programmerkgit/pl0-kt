package org.example.automaton

/**
 *  + is addition of operator category
 *  1 is integer of number category
 */
enum class TokenCategory {
    Operator,
    Number,
    Blank,
    Identifier,
    Symbol,
    Start,
    End,
    PositiveClosure,
    Closure,
    LeftBrace,
    RightBrace
}
