package org.example.automaton

class Token(
    val matcher: TokenMatcher,
    val text: String,
    var value: Any = text,
    var type: String = ""
) {
}
