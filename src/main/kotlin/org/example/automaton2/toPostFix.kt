package org.example.automaton2

/**
 * convert "a.b.(c.d|e.f)*" to "ab.cd.ef.|*."
 * @param[pattern] Pattern should be preprocessed pattern.
 *
 */
fun toPostFix(pattern: String): String {
    var output = ""
    val opStack: MutableList<Char> = mutableListOf()
    fun popStack(c: Char) {
        if (opStack.isEmpty()) return
        val top = opStack.last()
        if (top == '(' || top == ')') return

        val a = checkNotNull(opPrecedence[top]) { "org.example.automaton2.getOpPrecedence is not defined for $top" }
        val b = checkNotNull(opPrecedence[c]) { "org.example.automaton2.getOpPrecedence is not defined for $c" }
        if (a >= b) {
            output += opStack.removeAt(opStack.lastIndex)
            popStack(c)
        }
    }
    pattern.forEachIndexed { _, c ->
        when (c) {
            /* ( has least precedence */
            '(' -> opStack.add(c)
            /* Pop until '(' appears */
            ')' -> {
                if (opStack.isNotEmpty()) {
                    while (opStack.last() != '(') {
                        output += opStack.removeAt(opStack.lastIndex)
                    }
                    /* ( and ) pair is discarded */
                    opStack.removeAt(opStack.lastIndex)
                } else {
                    throw Error("No corresponding (")
                }
            }
            in setOf('+', '*', '?', '|', '.') -> {
                popStack(c)
                opStack.add(c)
            }
            else -> output += c
        }
    }
    while (opStack.isNotEmpty()) {
        output += opStack.removeAt(opStack.lastIndex)
    }
    return output
}
