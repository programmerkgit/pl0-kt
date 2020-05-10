package org.example.pl0_2

fun block(token: Token) {
    while (token is ConstToken || token is VarToken || token is Fun)
}