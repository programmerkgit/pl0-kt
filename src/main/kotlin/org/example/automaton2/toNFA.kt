package org.example.automaton2

fun toNFA(pattern: String): NFA {
    if (pattern.isEmpty())
        return NFA()
    val postFix = toPostFix(preprocessPattern(pattern))
    val stack = mutableListOf<NFA>()
    postFix.forEach { c ->
        when (c) {
            in setOf('+', '*', '?') -> {
                val top = stack.removeAt(stack.lastIndex)
                when (c) {
                    '+' -> stack.add(top.positiveClosure())
                    '*' -> stack.add(top.closure())
                    '?' -> stack.add(top.nullable())
                }
            }
            in setOf('|', '.') -> {
                val b = stack.removeAt(stack.lastIndex)
                val a = stack.removeAt(stack.lastIndex)
                when (c) {
                    '|' -> stack.add(a or b)
                    '.' -> stack.add(a + b)
                }
            }
            else -> {
                stack.add(NFA(c))
            }
        }
    }
    return stack.last()
}
