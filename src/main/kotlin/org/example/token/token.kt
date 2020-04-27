package org.example.token
class Token(
    val matcher: TokenMatcher,
    val text: String,
    var value: Any = text,
    var type: String = ""
) {
}
