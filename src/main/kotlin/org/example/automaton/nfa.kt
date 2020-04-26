package org.example.automaton

import java.lang.Error

class R(text: String) {
}

operator fun Regex.contains(text: String): Boolean = this.matches(text)

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

