package org.example.token

enum class TokenMatcher(val pattern: Regex, val category: TokenCategory) {
    Multiply("\\*", TokenCategory.Operator),
    Division("/", TokenCategory.Operator),
    Addition("\\+", TokenCategory.Operator),
    Subtract("-", TokenCategory.Operator),
    Assign(":=", TokenCategory.Operator),
    WhiteSpace("\\s+", TokenCategory.Blank),
    Identifier("[a-zA-Z]\\w*", TokenCategory.Identifier),
    Float("[0-9]+\\.[0-9]+", TokenCategory.Number),
    Integer("-?([1-9][0-9]*|[0-9])", TokenCategory.Number);// 「|」では最初にマッチしてしまうので、長いのを手前に

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

val TokenMatchers = listOf<TokenMatcher>(
    TokenMatcher.Addition,
    TokenMatcher.Multiply,
    TokenMatcher.WhiteSpace,
    TokenMatcher.Subtract,
    TokenMatcher.Division,
    TokenMatcher.Identifier,
    TokenMatcher.Assign,
    TokenMatcher.Float,
    TokenMatcher.Integer
)