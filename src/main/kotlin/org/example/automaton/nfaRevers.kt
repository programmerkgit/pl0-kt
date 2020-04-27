package org.example.automaton

operator fun Regex.contains(char: Char): Boolean = this.matches(char.toString())
fun greaterAThanB(a: Char, b: Char): Boolean {
    return (priorities[a] ?: error("NO OP $a")) >= (priorities[b] ?: error("NO OP $b"))
}

fun MutableList<Char>.pop() = this.removeAt(this.lastIndex)


fun createReverseStack(regexString: String): String {

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

    fun whenRightBrace(char: Char) {
        /* 優先順位の高いOpを再帰的にスタックから降ろして出力する */
        val lastOp = opStack.pop()
        resultStack.add(lastOp)
        if (lastOp == '(') {
            resultStack.add(char)
        } else {
            whenRightBrace(char)
        }
    }

    fun whenLeftBrace(char: Char) {
        /* 優先順位の高いOpを再帰的にスタックから降ろして出力する */
        popLeftBrace(char)
        opStack.add(char)
    }

    fun whenSymbolChar(char: Char, preChar: Char?) {
        /* + opの処理 */
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
            '*' -> whenOperator(char)
            '_' -> whenOperator(char)
            '|' -> whenOperator(char)
            '(' -> whenLeftBrace(char)
            ')' -> whenRightBrace(char)
            in Regex("^[a-zA-Z]$") -> {
                whenSymbolChar(char, preChar)
            }
        }
        preChar = char
    }
    return resultStack.joinToString("")
}


