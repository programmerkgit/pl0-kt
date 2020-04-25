package org.example.easyCompiler

import org.example.token.TokenMatcher

fun main() {
    parse(inputLine)
}

/**
 * should return tokens
 * */
fun parse(line: String) {
    val matchers = listOf<TokenMatcher>(
        TokenMatcher.Addition,
        TokenMatcher.Multiply,
        TokenMatcher.WhiteSpace,
        TokenMatcher.Subtract,
        TokenMatcher.Division,
        TokenMatcher.Identifier,
        TokenMatcher.Assign,
        TokenMatcher.Number
    )

    var remainingText = inputLine
    while (remainingText.isNotEmpty()) {
        val matchedToken = checkNotNull(matchers.mapNotNull { it.parse(remainingText) }.maxBy { it.value.length })
        remainingText = remainingText.slice(matchedToken.value.length until remainingText.length)
    }
}
