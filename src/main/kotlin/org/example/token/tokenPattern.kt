package org.example.token

enum class TokenPattern(val pattern: String, val category: TokenCategory) {
    Multiply("*", TokenCategory.Operator),
    Addition("+", TokenCategory.Operator),
    Subtract("-", TokenCategory.Operator),
}