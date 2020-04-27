package org.example.automaton

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

/* example */
// val a = org.example.automaton.NFARegex("a") or org.example.automaton.NFARegex("b") + org.example.automaton.NFARegex("c").closure()
