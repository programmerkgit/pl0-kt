package org.example.LL1Parser

import java.lang.Error


fun main() {

}

var nextToken: Char = ' '

var plusOp = '+'
var multiOp = '*'
var identifier = 'i'
var leftPar = '('
var rightPar = ')'

fun getNextChar(): Char {
    return ' '
}

fun putChar(char: Char) {

}

/*
* E -> T{+T[+]}
* T -> F {*F[*]}
* F -> (E)|i[i]
* */

fun E() {
    T()
    while (nextToken == plusOp) {
        nextToken = getNextChar()
        T()
        putChar(plusOp)
    }
}

fun T() {
    F()
    while (nextToken == multiOp) {
        nextToken = getNextChar()
        F()
        putChar(multiOp)
    }
}

fun F() {
    if (nextToken == leftPar) {
        nextToken = getNextChar()
        E()
        if (nextToken == rightPar) {
            nextToken = getNextChar()
        } else {
            unexpectedToken(nextToken, rightPar)
        }
    } else {
        if (nextToken == identifier) {
            putChar(identifier)
            nextToken = getNextChar()
        } else {
            unexpectedToken(nextToken, identifier)
        }
    }
}

fun unexpectedToken(char: Char, expected: Char) {
    throw Error("Unexpected Token $char. expected $expected")
}