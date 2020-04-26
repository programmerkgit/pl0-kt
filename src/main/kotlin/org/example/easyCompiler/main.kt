package org.example.easyCompiler

import org.example.token.Token
import org.example.token.TokenMatcher
import org.example.token.TokenMatchers

fun main() {
    val matchedTokens: List<Token> = parse(inputLine, TokenMatchers)
    matchedTokens.forEach {
        println("${it.matcher.category}: ${it.text}")
    }
}

/**
 * should return tokens
 * */
fun parse(line: String, matchers: List<TokenMatcher>): List<Token> {
    var remainingText = line
    val matchedTokens: MutableList<Token> = mutableListOf()
    while (remainingText.isNotEmpty()) {
        val matchedToken = checkNotNull(matchers.mapNotNull { it.parse(remainingText) }.maxBy { it.text.length })
        /**
         * skip if white space
         * */
        if (matchedToken.matcher !== TokenMatcher.WhiteSpace) {
            matchedTokens.add(matchedToken)
        }
        remainingText = remainingText.slice(matchedToken.text.length until remainingText.length)
    }
    return matchedTokens
}

/* should return register transfer language, which can be converted to assembly */
fun reverseTokens(tokens: List<Token>): List<Token> {
    val reversed: MutableList<Token> = mutableListOf<Token>();
    return reversed
}