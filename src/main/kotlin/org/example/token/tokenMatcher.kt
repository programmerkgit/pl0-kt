package org.example.token


/**
 *
 * */
open class TokenMatcher(
    val pattern: Regex,
    val tokenType: String,
    val category: TokenCategory
) {
    fun parse(text: String, i: Int): Token? {
        val matchResult = pattern.find(text, i)
        val first = matchResult?.range?.first
        return if (first == i) {
            return Token(pattern, category, tokenType, matchResult.value)
        } else {
            null
        }
    }
}