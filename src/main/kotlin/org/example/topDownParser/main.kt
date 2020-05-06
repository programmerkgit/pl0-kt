package org.example.topDownParser

import org.example.LL1Parser.getNextChar
import org.example.pl0.Token


fun first(str: String): List<Any> {
    return listOf()
}

fun first(strings: List<String>): List<String> {
    return listOf()
}

var nextToken: Any = ""
fun getNextToken(): Token {
    TODO("unexpected")
}

fun block() {
    /* {(constDec|varDec|funcDec)}statement */
    while (nextToken in first("constDec|verDec|funcDec")) {
        when (nextToken) {
            in first("constDec") -> constDec()
            in first("verDec") -> varDec()
            in first("funcDec") -> funcDec()
            else -> error("no match")
        }
    }
    statement()
}

fun constDec() {
    // T("const {ident = number//,};")
    // T("const")
    if (nextToken == "const") {
        nextToken = getNextToken()
    } else {
        error("expected const")
    }
    // T("{ident = number//,}")
    while (true) {
        // T("ident = number")
        // T("Ident")
        if (nextToken == "ident") {
            nextToken = getNextToken()
        } else {
            error("expected identifier")
        }
        T("=")
        if (nextToken == "=") {
            nextToken = getNextToken()
        } else {
            error("expected =")
        }

        if (nextToken == "number") {
            nextToken = getNextToken()
        } else {
            error("expected number")
        }
        if (nextToken !in first("ident = number")) break
        if (nextToken == ",") {
            nextToken = getNextToken()
        } else {
            error("expected ,")
        }
    }
    // T(",")
    if (nextToken == ";") {
        nextToken = getNextToken()
    } else {
        error(";")
    }
}

fun T(a: Any) {

}

fun varDec() {}
fun funcDec() {}
fun statement() {}
