package org.example.automaton.nfa

import org.example.automaton.nfa.Token
import org.example.automaton.nfa.TokenCategory


enum class TokenMatcher(val pattern: Regex, val category: TokenCategory) {
    Closure("*", TokenCategory.Closure),

    // PositiveClosure("\\+", TokenCategory.PositiveClosure),
    Symbol("[a-zA-Z]", TokenCategory.Symbol),
    LeftBrace("\\(", TokenCategory.LeftBrace),
    Start("\\^", TokenCategory.Start),
    End("\\$", TokenCategory.End),
    RightBrace("\\)", TokenCategory.RightBrace);
    // Division("/", TokenCategory.Operator),
    // Subtract("-", TokenCategory.Operator),
    // Assign(":=", TokenCategory.Operator),
    // WhiteSpace("\\s+", TokenCategory.Blank),
    // Float("[0-9]+\\.[0-9]+", TokenCategory.Number),
    // Integer("-?([1-9][0-9]*|[0-9])", TokenCategory.Number);// 「|」では最初にマッチしてしまうので、長いのを手前に

    constructor(pattern: String, category: TokenCategory) : this(Regex(pattern), category)

    /**
     * parse text and return token
     * find pattern from start of line
     * */
    fun parse(text: String): Token? {
        val matchResult = Regex("^${pattern.pattern}").find(text)
        return if (matchResult !== null) {
            return Token(this, matchResult.value)
        } else {
            null
        }
    }
}