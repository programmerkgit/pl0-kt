package org.example.token

class Token(
    val pattern: Regex,
    val category: TokenCategory,
    val name: String,
    val value: String,
) {
    constructor(
        pattern: String,
        category: TokenCategory,
        name: String,
        value: String
    ) : this(
        Regex(pattern),
        category,
        name,
        value
    ) {
    }
}
