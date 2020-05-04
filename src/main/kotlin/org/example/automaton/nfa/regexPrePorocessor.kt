package org.example.automaton.nfa

private fun insertConcatOperator(pattern: String): String {
    var output = ""
    pattern.forEachIndexed { i, char ->
        output += char
        when (char) {
            /* When shouldn't insert concat operator */
            in setOf('|', '(') -> return@forEachIndexed
            /* When should insert concat operator */
            else -> {
                if (i < pattern.length - 1) {
                    val lookahead = pattern[i + 1]
                    /* 次の文字 */
                    if (lookahead !in setOf('|', '*', ')', '+', '?')) {
                        output += "."
                    }
                }
            }
        }
    }
    return output
}

internal fun preprocessPattern(pattern: String): String {
    return insertConcatOperator(pattern)
}