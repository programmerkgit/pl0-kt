package org.example.automaton

import java.lang.Error

class R(text: String) {
}

operator fun Regex.contains(char: Char): Boolean = this.matches(char.toString())

/* state */
/* function */
/* state and function */
/* NFA state and function and final and start*/
/* concat */
/* | */
/* loop */
/* アルファベットは+ operatorと文字の二つを読んだと解釈 */
/* (は優先順位が最低なのでOperatorをポップ */
/* *,?, +は優先順位が最高。 */
/* ^,$も優先順位最低 */
val regexPattern: String = "ab|(cd)*ef"


fun nfa(regexString: String) {
    val targetText = "^$regexString$"
    val opStack: MutableList<Char> = mutableListOf()
    val resultStack: MutableList<Char> = mutableListOf()
    fun whenEndChar() {

    }

    fun whenOrChar() {

    }

    fun whenBraceChar() {

    }

    fun whenEndBraceChar() {

    }

    fun pushStack(char: Char) {

    }

    fun whenTimesChar() {

    }

    fun whenPositiveClosureChar() {}

    for (char: Char in regexString) {
        when (char) {
            '^' -> opStack.add(char)
            '$' -> {
                whenEndChar()
            }
            in Regex("^[a-zA-Z]$") -> {
                if (resultStack.size > 0) {
                    pushStack(char)
                }
                resultStack.add(char)
            }
            '*' -> {
                whenTimesChar()
            }
            '+' -> {
                whenPositiveClosureChar()
            }
            '|' -> {
                whenOrChar()
            }
            '(' -> {
                whenBraceChar()
            }
            ')' -> {
                whenEndBraceChar()
            }
        }
    }
}


class NFARegex(val pattern: String) {
    operator fun plus(nfaRegex: NFARegex): NFARegex {
        return NFARegex(pattern + nfaRegex.pattern);
    }

    fun closure(): NFARegex {
        return NFARegex("$pattern*")
    }

    infix fun or(nfaRegex: NFARegex): NFARegex {
        return NFARegex("$pattern|${nfaRegex.pattern}")
    }
}

val a = NFARegex("a") or NFARegex("b") + NFARegex("c").closure()

fun main() {
    println(a.pattern)
}

