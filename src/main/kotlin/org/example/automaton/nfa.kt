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
// val a = NFARegex("a") or NFARegex("b") + NFARegex("c").closure()
