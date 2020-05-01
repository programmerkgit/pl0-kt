package org.example.automaton.nfa

operator fun Regex.contains(char: Char): Boolean = this.matches(char.toString())

fun greaterAThanB(a: Char, b: Char): Boolean {
    return (priorities[a] ?: error("NO OP $a")) >= (priorities[b] ?: error("NO OP $b"))
}

fun MutableList<Char>.pop() = this.removeAt(this.lastIndex)


fun reverseRegex(regexString: String): String {

    val targetText = "^$regexString$"
    val opStack: MutableList<Char> = mutableListOf()
    val resultStack: MutableList<Char> = mutableListOf()

    fun popStack(char: Char) {
        /* 優先順位の高いOpを再帰的にスタックから降ろして出力する */
        if (greaterAThanB(opStack.last(), char)) {
            resultStack.add(opStack.pop())
            popStack(char)
        }
    }

    fun popLeftBrace(char: Char) {
        if (greaterAThanB(opStack.last(), char) && opStack.last() != '(') {
            resultStack.add(opStack.pop())
            popLeftBrace(char)
        }
    }

    /*  */
    fun whenOperator(char: Char) {
        popStack(char)
        opStack.add(char)
    }

    fun popBraces() {
        val lastOp = opStack.pop()
        resultStack.add(lastOp)
        if (lastOp != '(') {
            popBraces()
        }
    }

    fun whenLeftBrace(char: Char, preChar: Char?) {
        /* 優先順位の高いOpを再帰的にスタックから降ろして出力する */
        popLeftBrace(char)
        if (preChar != '(' && preChar != '^' && preChar != '|') {
            opStack.add('_')
        }
        opStack.add(char)
    }

    fun whenPreOperator(char: Char) {
        resultStack.add(char)
    }

    fun whenSymbolChar(char: Char, preChar: Char?) {
        /* + opの処理 */
        if (opStack.last() == ')') {
            popBraces()
        }
        if (preChar != '(' && preChar != '^' && preChar != '|') {
            whenOperator('_')
        }
        /* 出力 */
        resultStack.add(char)

    }

    var preChar: Char? = null
    for (char: Char in targetText) {
        when (char) {
            '^' -> opStack.add(char)
            '$' -> whenOperator(char)
            '*' -> whenPreOperator(char)
            '_' -> whenOperator(char)
            '+' -> whenOperator(char)
            '|' -> whenOperator(char)
            '(' -> whenLeftBrace(char, preChar)
            ')' -> whenOperator(char)
            in Regex("^[a-zA-Z]$") -> {
                whenSymbolChar(char, preChar)
            }
        }
        preChar = char
    }
    return resultStack.joinToString("")
}


